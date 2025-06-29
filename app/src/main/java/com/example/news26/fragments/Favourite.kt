package com.example.news26.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.example.news26.NewsViewModel
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news26.NetworkLiveData
import com.example.news26.R
import com.example.news26.adapters.AdapterR1
import com.example.news26.adapters.RoomAdapter
import com.example.news26.databinding.FragmentFavouriteBinding
import com.example.news26.databinding.FragmentHomeBinding
import com.example.news26.util.Resource
import com.google.android.material.snackbar.Snackbar


class Favourite : Fragment() {


    private  var _binding: FragmentFavouriteBinding? = null
    private val binding  get() = _binding!!


    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var adpater : RoomAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavouriteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adpater = RoomAdapter()

         setUpRecycleView()

//        observing the network no need stored in database

        viewModel.getFavNews().observe(viewLifecycleOwner) {
            articles ->
              adpater.differ.submitList(articles)
              adpater.setOnClickListener { roomArticle ->
                 val bundle = Bundle().apply {
                     putSerializable("roomArticle",roomArticle)
                 }
                  val webFav = WebFav()
                  webFav.arguments = bundle

                  parentFragmentManager.beginTransaction().replace(R.id.fragment_container,webFav)
                      .addToBackStack(null)
                      .commit()
              }

        }

//        swipe to delete
        val itemTouchHelperCallback = object  : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                val position = viewHolder.absoluteAdapterPosition
                if (position != RecyclerView.NO_POSITION)
                {
                    val roomArticle = adpater.getArticleAt(position)

                    when(direction){
                        ItemTouchHelper.LEFT ->{
                           viewModel.deleteArticle(roomArticle)
                           Snackbar.make(view, "Deleted", Snackbar.LENGTH_LONG).apply {
                               setAction("Undo") {
                                   viewModel.addToFav(roomArticle)
                               }
                               show()
                           }
                       }
                       }
                    }

                }
            }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.favRecycleView)

        }




    fun setUpRecycleView(){
        binding.favRecycleView.layoutManager = LinearLayoutManager(requireContext())  // gives the context of the host
        binding.favRecycleView.adapter = adpater
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
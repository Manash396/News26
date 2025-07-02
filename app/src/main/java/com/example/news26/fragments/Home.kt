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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news26.NetworkLiveData
import com.example.news26.R
import com.example.news26.adapters.AdapterR1
import com.example.news26.databinding.FragmentHomeBinding
import com.example.news26.util.Resource



class Home : Fragment() {


    private  var _binding: FragmentHomeBinding? = null
    private val binding  get() = _binding!!


    private var isScrolling  = false

    private val viewModel: NewsViewModel by activityViewModels()

    private lateinit var noInternetView : TextView
    private lateinit var progressBarView : ProgressBar
    private lateinit var adpater : AdapterR1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (savedInstanceState != null) return
        adpater = AdapterR1()

        setUpRecycleView()
        noInternetView = binding.noInternet
        progressBarView = binding.progressBar


//        observing the network

        NetworkLiveData(requireActivity().application).observe(viewLifecycleOwner) {
            result ->
            if (result){
                   binding.noInternet.visibility = View.GONE
                if(viewModel.headLineResponse == null)
                    viewModel.getHeadLines("us")
            }else{
                binding.noInternet.visibility = View.VISIBLE
            }
        }

        viewModel.headLines.observe(viewLifecycleOwner) { result ->
            when(result){
               is Resource.Success -> {
                   noInternetView.visibility = View.GONE
                   progressBarView.visibility = View.GONE
                    result.data?.let {
                        val articles = viewModel.headLineResponse?.articles
//                        val articles = it.articles
                        adpater.differ.submitList(articles)
                        // setting the listener
                        adpater.setOnItemClickedListener { article ->
                            val bundle = Bundle().apply {
                                putSerializable("article", article)
                            }
                            val webFragment = Web()
                            webFragment.arguments = bundle


                            parentFragmentManager.beginTransaction().replace(R.id.fragment_container,webFragment)
                                .addToBackStack(null)
                                .commit()

                        }
                    }
                }

                is Resource.Error ->{
                    noInternetView.visibility = View.VISIBLE
                    progressBarView.visibility = View.GONE
                    noInternetView.text = result.message
                }
                is Resource.Loading -> {
                    progressBarView.visibility = View.VISIBLE
                    noInternetView.visibility = View.GONE
                }
            }
        }
    }

    fun setUpRecycleView(){
        val layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecycleView.layoutManager = layoutManager  // gives the context of the host
        binding.homeRecycleView.adapter = adpater
   // setting scroll view listener

        binding.homeRecycleView.addOnScrollListener(object  : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount  = layoutManager.childCount
                val totalItem = layoutManager.itemCount

                val shouldPaginate = isScrolling && (firstVisibleItemPosition+visibleItemCount >= totalItem) && firstVisibleItemPosition>=0

                if (shouldPaginate){
                    isScrolling = false
                    viewModel.getHeadLines("us")
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    isScrolling = true
                }
            }
        })


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding =null
    }


}
package com.example.news26.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news26.R
import com.example.news26.adapters.RoomAdapter.RoomViewHolderClass
import com.example.news26.db.RoomArticle

class RoomAdapter : RecyclerView.Adapter<RoomViewHolderClass>()  {
    inner class RoomViewHolderClass(view: View) : RecyclerView.ViewHolder(view)
    private lateinit var image : ImageView
    private lateinit var title : TextView
    private lateinit var publish : TextView
    private lateinit var describe : TextView

    private val differCallBack = object : DiffUtil.ItemCallback<RoomArticle>(){
        override fun areItemsTheSame(
            oldItem: RoomArticle,
            newItem: RoomArticle
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: RoomArticle,
            newItem: RoomArticle
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoomViewHolderClass {
        val viewItems = LayoutInflater.from(parent.context).inflate(R.layout.item_article,parent,false)
        return RoomViewHolderClass(viewItems)
    }

    private var onFavClickListener : ((RoomArticle) -> Unit)? = null

    override fun onBindViewHolder(
        holder: RoomViewHolderClass,
        position: Int
    ) {
        val roomArticle = differ.currentList[position]
        image = holder.itemView.findViewById(R.id.imageArticle)
        title = holder.itemView.findViewById(R.id.titleArticle)
        publish = holder.itemView.findViewById(R.id.publishArticle)
        describe = holder.itemView.findViewById(R.id.describeArticle)

        holder.itemView.apply {
            Glide.with(this).load(roomArticle.urlToImage).into(image)
            title.text = roomArticle.title
            publish.text = roomArticle.publishedAt
            describe.text = roomArticle.description

            setOnClickListener {
                onFavClickListener?.invoke(roomArticle)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setOnClickListener(listener: (RoomArticle) -> Unit){
        onFavClickListener = listener
    }

    fun getArticleAt(position: Int): RoomArticle = differ.currentList[position]

}
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
import com.example.news26.adapters.AdapterR1.ViewHolderClass
import com.example.news26.data.Article


class AdapterR1: RecyclerView.Adapter<ViewHolderClass>() {

     inner class ViewHolderClass(view: View): RecyclerView.ViewHolder(view)

    private lateinit var image : ImageView
    private lateinit var title : TextView
    private lateinit var publish : TextView
    private lateinit var describe : TextView

    private val differCallback = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolderClass {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article,parent,false)
        return ViewHolderClass(view)
    }



    override fun getItemCount(): Int {
        return differ.currentList.size
    }

  private var onItemClickListener : ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val article = differ.currentList[position]

        image = holder.itemView.findViewById(R.id.imageArticle)
        title = holder.itemView.findViewById(R.id.titleArticle)
        publish = holder.itemView.findViewById(R.id.publishArticle)
        describe = holder.itemView.findViewById(R.id.describeArticle)

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(image)
            title.text = article.title
            publish.text = article.publishedAt
            describe.text = article.description

        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(article)
        }

    }

    fun setOnItemClickedListener(listener : (Article) -> Unit){
        onItemClickListener = listener
    }


}
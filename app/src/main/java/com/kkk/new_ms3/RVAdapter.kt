package com.kkk.new_ms3

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RVAdapter(private val context: Context) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    var datas = mutableListOf<ProfileData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item,parent,false)

        return ViewHolder(view)
    }

    interface ItemClick
    {
        fun onClick(view: View, position: Int)
    }
    var itemClick : ItemClick?=null

    override fun onBindViewHolder(holder: RVAdapter.ViewHolder, position: Int) {

        if(itemClick!=null) {
            //itemView.의 . 이 error라서 ?붙여줌
            holder?.itemView?.setOnClickListener { v->
                itemClick!!.onClick(v,position)
            }
        }
        holder.bind(datas[position],position)
    }

    override fun getItemCount(): Int = datas.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val singername: TextView = itemView.findViewById(R.id.singername)
        private val singerphoto: ImageView = itemView.findViewById(R.id.singerphoto)
        private val commentCount: TextView = itemView.findViewById(R.id.tv_comment_count)
        private val singerCrown : ImageView = itemView.findViewById(R.id.crown)
        private val singerRanking: TextView = itemView.findViewById(R.id.rankingText)
        private val bestCommentDecorator: LinearLayout = itemView.findViewById(R.id.best_comment_decorator)
        private val bestMedal : RelativeLayout = itemView.findViewById(R.id.best_medal)
        private val manyComment: TextView = itemView.findViewById(R.id.manyComment)



        fun bind(item : ProfileData,position: Int){
            singername.text = item.name
            Glide.with(itemView).load(item.img).into(singerphoto)
            commentCount.text = item.commentCount.toString()

            var cc = item.commentCount;
            if(cc in 10000..19999) {
                manyComment.text = "1만\n달성"
                bestMedal.visibility = View.VISIBLE
            } else if (cc in 20000..29999) {
                manyComment.text = "2만\n달성"
                bestMedal.visibility = View.VISIBLE
            } else if (cc in 30000..39999) {
                manyComment.text = "3만\n달성"
                bestMedal.visibility = View.VISIBLE
            } else if (cc in 40000..49999) {
                manyComment.text = "4만\n달성"
                bestMedal.visibility = View.VISIBLE
            } else if (cc in 50000..59999) {
                manyComment.text = "5만\n달성"
                bestMedal.visibility = View.VISIBLE
            } else if (cc in 60000..69999) {
                manyComment.text = "6만\n달성"
                bestMedal.visibility = View.VISIBLE
            } else if (cc in 70000..79999) {
                manyComment.text = "7만\n달성"
                bestMedal.visibility = View.VISIBLE
            } else if (cc in 80000..89999) {
                manyComment.text = "8만\n달성"
                bestMedal.visibility = View.VISIBLE
            } else if (cc in 90000..99999) {
                manyComment.text = "9만\n달성"
                bestMedal.visibility = View.VISIBLE
            } else if (cc in 100000..199999) {
                manyComment.text = "10만\n달성"
                bestMedal.visibility = View.VISIBLE
            }
            else {
                manyComment.text = null
                bestMedal.visibility = View.GONE
            }


            if(position==0) {
                singerCrown.setImageResource(R.drawable.goldcrown)
                singerRanking.text="응원댓글 1위    "
                bestCommentDecorator.visibility = View.VISIBLE
            } else if(position==1) {
                singerCrown.setImageResource(R.drawable.silvercrown)
                singerRanking.text="응원댓글 2위    "
                bestCommentDecorator.visibility = View.VISIBLE
            } else if(position==2) {
                singerCrown.setImageResource(R.drawable.bronzecrown)
                singerRanking.text="응원댓글 3위    "
                bestCommentDecorator.visibility = View.VISIBLE
            } else if(position==3) {
                singerCrown.visibility = View.GONE
                singerRanking.text="응원댓글 4위"
                bestCommentDecorator.visibility = View.VISIBLE
            } else if(position==4) {
                singerCrown.visibility = View.GONE
                singerRanking.text="응원댓글 5위"
                bestCommentDecorator.visibility = View.VISIBLE
            } else if(position==5) {
                singerCrown.visibility = View.GONE
                singerRanking.text="응원댓글 6위"
                bestCommentDecorator.visibility = View.VISIBLE
            } else if(position==6) {
                singerCrown.visibility = View.GONE
                singerRanking.text="응원댓글 7위"
                bestCommentDecorator.visibility = View.VISIBLE
            } else if(position==7) {
                singerCrown.visibility = View.GONE
                singerRanking.text="응원댓글 8위"
                bestCommentDecorator.visibility = View.VISIBLE
            } else if(position==8) {
                singerCrown.visibility = View.GONE
                singerRanking.text="응원댓글 9위"
                bestCommentDecorator.visibility = View.VISIBLE
            }
            else {
                singerCrown.setImageBitmap(null)
                singerRanking.text=null
                bestCommentDecorator.visibility = View.GONE
            }


        }

    }

}
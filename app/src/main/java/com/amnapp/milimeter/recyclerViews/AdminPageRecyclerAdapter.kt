package com.amnapp.milimeter.recyclerViews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.amnapp.milimeter.R
import com.amnapp.milimeter.UserData

class AdminPageRecyclerAdapter(var subUserlist: MutableList<UserData>): RecyclerView.Adapter<AdminPageRecyclerAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun onClicked(v: View, pos: Int)
    }
    interface OnEditClickListener{
        fun onClicked(v: View, pos: Int)
    }
    var itemListener: OnItemClickListener? = null
    var editListener: OnEditClickListener? = null
    fun setItemOnClickListener(listener: OnItemClickListener){
        this.itemListener = listener
    }
    fun setEditOnClickListener(listener: OnEditClickListener){
        this.editListener = listener
    }

    inner class ViewHolder(itemView: ViewGroup) : RecyclerView.ViewHolder(itemView) {
        val userNameTv = itemView.findViewById<TextView>(R.id.userNameTv)
        val militaryIdTv = itemView.findViewById<TextView>(R.id.militaryIdTv)
        val itemCv = itemView.findViewById<CardView>(R.id.itemCv)
        val editIb = itemView.findViewById<ImageButton>(R.id.editIb)

        init {//클릭이벤트 콜백 설정
            itemCv.setOnClickListener {
                itemListener?.onClicked(it, adapterPosition)
            }
            editIb.setOnClickListener {
                editListener?.onClicked(it, adapterPosition)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_admin_page,parent,false) as ViewGroup
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        subUserlist.get(position).let { item ->
            with(holder) {
                userNameTv.text = if(item.name == null) "탈퇴한 계정입니다" else item.name
                militaryIdTv.text = item.militaryId.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return subUserlist.size
    }
}
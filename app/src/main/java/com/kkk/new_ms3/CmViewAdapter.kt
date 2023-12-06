package com.kkk.new_ms3

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class CmViewAdapter (val items : MutableList<CmData>,
                     val blockedList : ArrayList<String>,
//                     val clearList : ArrayList<String>,
                     val context: Context
) : RecyclerView.Adapter<CmViewAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CmViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cm_item,parent,false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: CmViewAdapter.ViewHolder, position: Int) {

        holder.bindItems(items[position])

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){


        fun bindItems(cmData : CmData)
        {

            val cmNickname: TextView = itemView.findViewById(R.id.cmid)
            cmNickname.text = cmData.username
            //itemView?. 이후 오류
            val cmwrite: TextView = itemView?.findViewById(R.id.cmwrite)!!

            if(blockedList.contains(cmData.username)) {
                cmwrite!!.text = "차단된 사용자의 댓글입니다."
                cmNickname!!.visibility = View.GONE
            } else {
                cmwrite!!.text = cmData.say
                cmNickname!!.visibility = View.VISIBLE
            }

            val cmdate: TextView = itemView.findViewById(R.id.cmDate)
            cmdate.text = cmData.date

            val report: TextView = itemView.findViewById(R.id.reportButton)
            report.setOnClickListener {

                val selectorIntent = Intent(Intent.ACTION_SENDTO)
                selectorIntent.data = Uri.parse("mailto:")

                val emailIntent = Intent(Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("sookimy97@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "미스트롯3 응원하기 댓글신고")
                emailIntent.putExtra(Intent.EXTRA_TEXT,  "\n--------------------------------\n가수 이름 : ${cmData.singerId} \n 신고 댓글 닉네임: ${cmData.username} \n 신고 댓글 내용: ${cmData.say}\n--------------------------------\n위의 내용을 지우지 말고 보내주세요. 관리자 확인 후 2~3일 이내로 조치됩니다.")
                emailIntent.selector = selectorIntent

                startActivity(
                    itemView.context,
                    Intent.createChooser(emailIntent, "이메일 보내기"),
                    null
                )

            }

            val block: TextView = itemView.findViewById(R.id.blockButton)
            block.text = "차단"
            if(blockedList.contains(cmData.username)) {
                block.text = "해제"
            }
//            block.setOnClickListener {
//
//                AlertDialog.Builder(itemView.context)
//                    .setTitle("사용자 차단하기")
//                    .setMessage("해당 사용자를 차단하시겠습니까?")
//                    .setPositiveButton("확인") { dialog, which ->
//
//                        // 차단해서 차단된 댓글 아이디 저장하는 부분
//                        cmwrite.text="차단된 사용자의 댓글입니다."
//                        cmNickname!!.visibility = View.GONE
//                        blockedList.add(cmData.username)
//                        // 아래의 함수에서 리스트를 셰어드 프리퍼런스에 저장하는 역할을 함
//                        setStringArrayPref(context, "block", blockedList)
//                        notifyDataSetChanged()
//
//                    }
//                    .setNegativeButton("취소") { dialog, which -> dialog.dismiss() }
//                    .show()
//
//            }

            block.setOnClickListener {
                if(blockedList.contains(cmData.username)) {
                    AlertDialog.Builder(itemView.context)
                        .setTitle("차단 해제하기")
                        .setMessage("차단된 사용자를 해제하시겠습니까?")
                        .setPositiveButton("확인") { dialog, which ->
                            cmwrite!!.text = cmData.say
                            cmNickname!!.visibility = View.VISIBLE
                            blockedList.remove(cmData.username)
                            setStringArrayPref(context, "block", blockedList)
                            block.text = "차단"
                            notifyDataSetChanged()
                        }
                        .setNegativeButton("취소") { dialog, which -> dialog.dismiss() }
                        .show()
                } else {
                    AlertDialog.Builder(itemView.context)
                        .setTitle("사용자 차단하기")
                        .setMessage("해당 사용자를 차단하시겠습니까?")
                        .setPositiveButton("확인") { dialog, which ->

                            // 차단해서 차단된 댓글 아이디 저장하는 부분
                            cmwrite.text="차단된 사용자의 댓글입니다."
                            cmNickname!!.visibility = View.GONE
                            blockedList.add(cmData.username)
                            // 아래의 함수에서 리스트를 셰어드 프리퍼런스에 저장하는 역할을 함
                            setStringArrayPref(context, "block", blockedList)
                            block.text = "해제"
                            notifyDataSetChanged()

                        }
                        .setNegativeButton("취소") { dialog, which -> dialog.dismiss() }
                        .show()
                }
            }
        }
    }

    private fun setStringArrayPref(context: Context, key: String, commentIdList: ArrayList<String>) {
        val pref = context.getSharedPreferences("pref",0)
        val edit = pref.edit()
        val a = JSONArray()
        for (i in 0 until commentIdList.size) {
            a.put(commentIdList[i])
        }
        if (!commentIdList.isEmpty()) {
            edit.putString(key, a.toString())
        } else {
            edit.putString(key, null)
        }
        edit.apply()
    }
}
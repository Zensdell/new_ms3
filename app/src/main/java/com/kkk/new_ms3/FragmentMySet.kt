package com.kkk.new_ms3

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment


class FragmentMySet : Fragment() {

    private val TAG = "FragmentMySet"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(R.layout.fragment_my_set, container, false) as ViewGroup
        val pref = this.requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        val nickName = pref.getString("Nickname","")
        val edit = pref.edit()
        val nowId = view.findViewById<TextView>(R.id.currentNickname)

        if (nowId != null) {
            nowId.text=nickName
        }

        val changeBtn = view.findViewById<Button>(R.id.change_button)
        changeBtn?.setOnClickListener {
            val changeName = view.findViewById<EditText>(R.id.changeNickname)
            val nickname = changeName?.text.toString()

            if(nickname ==""){
                Toast.makeText(context, "닉네임을 정해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                edit.putString("Nickname", nickname)
                edit.apply()
                if (nowId != null) {
                    nowId.text = nickname
                }
                changeName!!.text.clear()
            }
        }

        val preferences = this.requireActivity().getSharedPreferences("CommentCount", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val psCmCount = preferences.getInt("CommentCount", 0) // "CommentCount" 키에 저장된 값 또는 기본값 0을 가져옵니다
        // val nowCmt = view.findViewById<TextView>(R.id.nowCommentCount)
        // nowCmt.text = psCmCount.toString()
        editor.apply()




//
//        val totalCommentCount = preferences.getInt("CommentCount",0)
//
////        var totalCommentCount = pref.getInt("CommentCount",0) //val commentCount 에다가 pref노트에서 commentCount에 해당하는 값을 넣는다. 만약에 commentCount가 아예 안적혀 있다면 0 을 넣는다.
//        val totalCount = totalCommentCount
//        val countEdit = pref.edit() //수정모드 변수가 Int값이면 Int자리에 변수를 넣어도 Int로 받는다
//        countEdit.putInt("CommentCount", totalCount)//첫번쨰는 키값, 2번은 실제 값
//        countEdit.apply() //값을 저장
//        val nowCmt = view.findViewById<TextView>(R.id.nowCommentCount)
//        nowCmt.text= totalCount.toString()
//        Log.d("cmt","$totalCount")



        val questionBtn = view.findViewById<TextView>(R.id.questionText1)
        questionBtn?.setOnClickListener {

            val selectorIntent = Intent(Intent.ACTION_SENDTO)
            selectorIntent.data = Uri.parse("mailto:")

            selectorIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("sookimy97@gmail.com"))
            selectorIntent.putExtra(Intent.EXTRA_SUBJECT, "미스트롯3 응원하기 문의 메일")
            selectorIntent.putExtra(Intent.EXTRA_TEXT,  "문의하실 사항을 적어주세요")

            startActivity(Intent.createChooser(selectorIntent, "이메일 보내기"))
        }

        val useSite = view.findViewById<TextView>(R.id.useRule1)
        useSite?.setOnClickListener {
            Log.d(TAG, "어??여기서 찍어봐야될거아냐 그래야 알수있는거아냐!!!! ")
            val uri = Uri.parse("https://sites.google.com/view/mstrot3use")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        val infoSite =view.findViewById<TextView>(R.id.infoRule1)
        infoSite?.setOnClickListener {
            Log.d(TAG, "어??여기서 찍어봐야될거아냐 그래야 알수있는거아냐2222222 ")
            val uri = Uri.parse("https://sites.google.com/view/zensprivacy/%ED%99%88")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        return view
    }




}
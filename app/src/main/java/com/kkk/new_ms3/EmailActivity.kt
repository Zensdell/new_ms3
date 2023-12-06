package com.kkk.new_ms3

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class EmailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        val singerId = intent.getStringExtra("singerId")
        val userName = intent.getStringExtra("username")
        val say = intent.getStringExtra("say")




        val sendButton = findViewById<Button>(R.id.sendBtn)
        sendButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "text/html"
            intent.data = Uri.parse("mailto: sookimy97@gmail.com")
            intent.putExtra(Intent.EXTRA_EMAIL, "")
            intent.putExtra(Intent.EXTRA_SUBJECT, "트롯가수 응원하기 댓글신고")
            intent.putExtra(Intent.EXTRA_TEXT, "--------------------------------\n가수 이름 : $singerId \n 신고 댓글 닉네임: $userName \n 신고 댓글 내용: $say\n--------------------------------\n위의 내용을 지우지 말고 보내주세요. 관리자 확인 후 2~3일 이내로 조치됩니다.")

            startActivity(intent)
        }

    }
}
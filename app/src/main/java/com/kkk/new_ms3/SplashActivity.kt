package com.kkk.new_ms3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        //        Log.d("SPLASH", auth.currentUser!!.uid)
        //Toast.makeText(this,"기존 비회원 로그인 회원입니다",Toast.LENGTH_LONG).show()
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)

//        try {
//
//            Log.d("SPLASH", auth.currentUser!!.uid)
//            //Toast.makeText(this,"기존 비회원 로그인 회원입니다",Toast.LENGTH_LONG).show()
//            Handler().postDelayed({
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
//            }, 2000)
//
//        } catch (e: Exception) {
//            Log.d("SPLASH", "회원가입 시켜줘야함")
//
//            auth.signInAnonymously()
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
//                        //Toast.makeText(this,"비회원 로그인 성공",Toast.LENGTH_LONG).show()
//
//                        Handler().postDelayed({
//                            startActivity(Intent(this, MainActivity::class.java))
//                            finish()
//                        }, 2000)
//
//                    }
//                    else {
//                        // If sign in fails, display a message to the user.
//                        Toast.makeText(this, "비회원 로그인 실패", Toast.LENGTH_LONG).show()
//                    }
//                }
//        }
    }
}
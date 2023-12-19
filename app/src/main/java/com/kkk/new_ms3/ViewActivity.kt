package com.kkk.new_ms3

import android.animation.ValueAnimator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONArray
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.widget.Toast

//import com.facebook.ads.AdSettings

import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_view.*
import kotlin.concurrent.thread

class ViewActivity : AppCompatActivity() {

    var isLiked=false
    private var rewardedAd: RewardedAd? = null
    val cmDataList = mutableListOf<CmData>()
    private lateinit var auth: FirebaseAuth
    private val TAG = "ViewActivity"
    private lateinit var cmViewAdapter: CmViewAdapter // 자료형을 받는다는 의미

    private var currentPageNumber = 0
    private val pageLimit:Long = 60
    private lateinit var llMore: LinearLayout
    private lateinit var lastVisible: DocumentSnapshot

    private lateinit var intentSingerId: String
    private lateinit var intentName: String
    private lateinit var intentImageUrl: String
    private lateinit var intentInfoUrl: String
    private lateinit var intentStageUrl: String
    private var intentCommentCount: Long = 0

    private var clearList: ArrayList<String>? = null
    private var blockedList: ArrayList<String>? = null
    private lateinit var cmEdit: EditText
    private lateinit var cmEditText: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        // AdSettings.addTestDevice("ea105a44-6bca-4355-b6af-7a0c65a508cf")
        cmDataList.clear()
        val recyclerView = findViewById<RecyclerView>(R.id.cm_rv)

//        clearList = getStringArrayPref(this,"clear") 해제 복귀 위한 clearList
//        if(clearList == null) {
//            clearList == ArrayList<String>()
//        }
        blockedList = getStringArrayPref(this, "block")
        if(blockedList == null) {
            blockedList = ArrayList<String>()
        }
        cmViewAdapter = CmViewAdapter(cmDataList,
            blockedList!!,
//                                      clearList!!,
            this) //여기가 cmViewAdapter에 값을 주는 부분
        recyclerView.adapter = cmViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        cmProgressBar.visibility = GONE

        llMore = findViewById<LinearLayout>(R.id.ll_more)
        val btnMore = findViewById<Button>(R.id.btn_more)
        btnMore.setOnClickListener {

            cmProgressBar.visibility = VISIBLE
            thread(start = true){
                Thread.sleep(1500)
                runOnUiThread {
                    cmProgressBar.visibility = GONE
                }
            }

            currentPageNumber++
            getCommentsFromFirestore()
        }


        // auth = Firebase.auth
        intentInfoUrl = intent.getStringExtra("infoUrl").toString()
        intentImageUrl = intent.getStringExtra("img").toString()
        intentStageUrl = intent.getStringExtra("stageUrl").toString()
        intentCommentCount = intent.getLongExtra("commentCount", 0)
        intentSingerId = intent.getStringExtra("singerId")!!
        intentName = intent.getStringExtra("name")!!

        MobileAds.initialize(this) {}

        var adRequest = AdRequest.Builder().build() //업로드 전 !!!펍코드 꼭 바꿀것!!!!
        RewardedAd.load(this,"ca-app-pub-8246055051187544/7596977895", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                Log.d("애드몹리워드광고로드실패","애드몹 리워드 못 받음")
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                Log.d("애드몹리워드광고로드","애드몹 리워드 받아오나?")
                rewardedAd = ad
            }
        })

        val cmButton = findViewById<ImageButton>(R.id.cmSave)
        cmButton?.setOnClickListener {         // 닉네임이 있는지 없는지 확인하고
            // if
            // 닉네임이 없을때
            val pref = getSharedPreferences("pref",0) //pref라는 메모장, Nickname이라고 기록한게 있는지 본다. "A" A값을 nickName에 받는다, 없으면 널값 ""을 받는다
            val nickName = pref.getString("Nickname","")
            if(nickName == ""){
                showEditDialog()
            } else {
                val commentCount = pref.getInt("CommentCount",0)

                Log.d("aaa","$commentCount")
                if(commentCount < 2){
                    sendComments()
                }
                else {
                    val builder = AlertDialog.Builder(this)
                    val inflater = layoutInflater
                    val dialogLayout = inflater.inflate(R.layout.adsalert, null)

                    builder.setView(dialogLayout)
                        .setPositiveButton("확인") {dialogInterface,which ->
                            admobSetting()
                        }
                        .setNegativeButton("취소") {dialogInterface,i->
                        }
                        .show()
                }
            }
            // 닉네임이 있을때
            // 댓글 firebase로 전송

            var view = this.currentFocus

            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }


        setButtonClickListener(intentImageUrl!!, intentStageUrl!!, intentInfoUrl!!, intentCommentCount!!)
//        getCommentsFromFirebase()

        getCommentsFromFirestore()

        setLikeButton()

        setCommentRuleAgree()


//        MediationTestSuite.launch(this,"ca-app-pub-8246055051187544~8203511508" )
        // AdSettings.addTestDevice("796e3543-c38d-4b34-a088-f1b238d2cea3")


    }


    private fun setCommentRuleAgree() {
        cmEdit = findViewById<EditText>(R.id.CmEditText)
        cmEdit.onFocusChangeListener = OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {

                val pref = getSharedPreferences("pref",0)
                val isRuleAgree = pref.getBoolean("isRuleAgree", false)
                if(!isRuleAgree) {
                    android.app.AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setTitle("댓글 이용 약관")
                        .setMessage("응원 댓글이 다음과 같을 경우 통보없이 댓글이 삭제되거나 응원댓글 작성에 제한을 받을 수 있습니다.\n\n *타인의 악의전인 비방이나 욕설이 있을 경우 \n *특정인의 명예훼손의 우려가 있는 경우 \n")
                        .setNeutralButton("이용약관 보기") {dialog, which ->
                            val uri = Uri.parse("https://sites.google.com/view/mstrot3use/")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                            cmEdit.clearFocus()

                        }
                        .setPositiveButton("동의") { dialog, which ->
                            val edit = pref.edit()
                            edit.putBoolean("isRuleAgree", true)
                            edit.apply()
                        }
                        .setNegativeButton("거절") { dialog, which ->
                            dialog.dismiss()
                            cmEdit.clearFocus()
                        }
                        .show()
                }
            }
        }
    }

    private fun setLikeButton() {
        val likeBtn = findViewById<LottieAnimationView>(R.id.like_btn)
        val pref = getSharedPreferences("pref",0)
        isLiked = pref.getBoolean(intentSingerId, false)
        var animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(500)

        Log.d(TAG, "setLikeButton: "+isLiked)

        if (isLiked){
            animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(500)
        } else {
            animator = ValueAnimator.ofFloat(0.5f, 1f).setDuration(100)

        }

        animator.addUpdateListener{
            likeBtn.progress = it.animatedValue as Float
        }
        animator.start()

    }

    private fun setButtonClickListener(img: String,  stageUrl: String, infoUrl:String, commentCount: Long) {
        val bigImage = findViewById<ImageView>(R.id.big_image)
//        bigImage.setImageResource(img)
        Glide.with(this).load(img).into(bigImage)

        val viewCommentCount = findViewById<Button>(R.id.view_comment_count)
        viewCommentCount.text = "댓글 "+commentCount.toString()+"개"
        viewCommentCount.setOnClickListener {
            Toast.makeText(this,"댓글로 가수를 응원해주세요.", Toast.LENGTH_SHORT).show()
        }

        val stageButton = findViewById<Button>(R.id.stage_button)
        stageButton.setOnClickListener {
            val intent = Intent(this, StageWebView::class.java)
            intent.putExtra("stageUrl", stageUrl)
            intent.putExtra("singerId",intentSingerId)
            startActivity(intent)
        }

        val infoButton = findViewById<Button>(R.id.infoBtn)
        infoButton.setOnClickListener {
            if(infoUrl == "") {
                Toast.makeText(this, "가수의 정보가 업데이트 되지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, InfoWebView::class.java)
                intent.putExtra("infoUrl", infoUrl)
                startActivity(intent)
            }
        }

        val voteButton = findViewById<Button>(R.id.voteBtn)
        voteButton.setOnClickListener {
            val intent = Intent(this, VoteWebView::class.java)
            startActivity(intent) //쿠팡 플레이 필요하다는 메세지

        }

        val shareButton = findViewById<Button>(R.id.shareBtn)
        shareButton.setOnClickListener { try {
            val sendText = "감동의 무대를 선사한 미스트롯3 함께 응원해요! 다운로드 - https://play.google.com/store/apps/details?id=com.kkk.new_ms3"
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, sendText)
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, "Share"))
        } catch (ignored: ActivityNotFoundException) {
            Log.d("test", "ignored : $ignored")
        }

        }

        val likeBtn = findViewById<LottieAnimationView>(R.id.like_btn)
        likeBtn.setOnClickListener {

            if(!isLiked) {

                val animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(500)
                animator.addUpdateListener {
                    likeBtn.progress = it.animatedValue as Float
                }
                animator.start()
                isLiked = true
                val pref = getSharedPreferences("pref",0)
                val edit = pref.edit()
                edit.putBoolean(intentSingerId,isLiked)
                edit.apply()
                likeDatas.add(
                    ProfileData(
                        img = intentImageUrl,
                        name = intentName,
                        stageUrl = intentStageUrl,
                        singerId = intentSingerId,
                        infoUrl = intentInfoUrl,
                        commentCount = intentCommentCount,
                    )
                )

            } else {
                val animator = ValueAnimator.ofFloat(0.5f, 1f).setDuration(100)
                animator.addUpdateListener{
                    likeBtn.progress = it.animatedValue as Float
                }
                animator.start()
                isLiked = false
                val pref = getSharedPreferences("pref",0)
                val edit = pref.edit()
                edit.putBoolean(intentSingerId,isLiked)
                edit.apply()


                val newLikeDatas :ArrayList<ProfileData> = ArrayList()
                likeDatas.forEach{
                    if(it.singerId != intentSingerId) {
                        newLikeDatas.add(it)
                    }
                }
                likeDatas = newLikeDatas
            }
        }
    }

    private fun sendComments() {
        cmEditText = cmEdit.text.toString()
        if (cmEditText != "") {


            val pref = getSharedPreferences("pref",0) //pref라는 메모장, Nickname이라고 기록한게 있는지 본다. "A" A값을 nickName에 받는다, 없으면 널값 ""을 받는다
            val nickName = pref.getString("Nickname","")


            val sdf = SimpleDateFormat("yyyy-M-dd HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            val currentDate = sdf.format(Calendar.getInstance().time)


            //Firestore
            val data = hashMapOf(
                "singerId" to intentSingerId,
                "say" to cmEditText,
                "date" to currentDate,
                "username" to nickName,

                )
            val db = Firebase.firestore
            db.collection(intentSingerId).add(data).addOnSuccessListener { documentReference ->



                cmDataList.add(0,
                    CmData(
                        username = nickName!!,
                        singerId = intentSingerId,
                        say = cmEditText,
                        date = currentDate,
                        commentId = documentReference.id
                    ))
                cmViewAdapter.notifyDataSetChanged()


                val singerRef = db.collection("Singers").document(intentSingerId) //한명에 대한 필드를 다 가져옴
                singerRef.get().addOnSuccessListener { result ->
                    val document = result.data // 이게 필드부분
                    var count: Long = document!!["commentCount"] as Long
                    Log.d(TAG, "sendComments: count? $count")
                    count += 1
                    singerRef.update("commentCount", count)



                }.addOnFailureListener {

                }

            }.addOnFailureListener { e -> }


            // 코멘트 카운트 +1 해주는 부분
            var commentCount = pref.getInt("CommentCount",0) //val commentCount 에다가 pref노트에서 commentCount에 해당하는 값을 넣는다. 만약에 commentCount가 아예 안적혀 있다면 0 을 넣는다.
            val totalCount = commentCount + 1
            Log.d("bbb","$commentCount")
            val edit = pref.edit() //수정모드 변수가 Int값이면 Int자리에 변수를 넣어도 Int로 받는다
            edit.putInt("CommentCount", totalCount)//첫번쨰는 키값, 2번은 실제 값
            edit.apply() //값을 저장
            cmEdit!!.text.clear()
        }
    }

    private fun admobSetting() {




        rewardedAd?.let { ad ->
            ad.show(this, OnUserEarnedRewardListener { rewardItem ->
                // Handle the reward.
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Log.d("TAG", "리워드 받았다!!!" + rewardAmount)
                val pref = getSharedPreferences("pref", 0)
                val edit = pref.edit() //수정모드
                edit.putInt("CommentCount", 0) //첫번쨰는 키값, 2번은 실제 값
                edit.apply() //값을 저장
                Toast.makeText(this@ViewActivity, "댓글응원권 2개 획득!", Toast.LENGTH_LONG).show()
            })
        } ?: run {
            val pref = getSharedPreferences("pref", 0)
            val edit = pref.edit() //수정모드
            edit.putInt("CommentCount", 0) //첫번쨰는 키값, 2번은 실제 값
            edit.apply() //값을 저장
            Log.d("TAG", "The rewarded ad wasn't loaded yet.")
            Log.d(TAG, "The rewarded ad wasn't ready yet.")

            // Load ad one more >.< 광고 로드 실패시 로드 한 번 더 시도하기
            var adRequest = AdRequest.Builder().build()
            RewardedAd.load(this,"ca-app-pub-8246055051187544/7596977895", adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Ad was loaded.")
                    rewardedAd = ad
                }
            })
        }

        // Load ad one more >.< 그냥 로드 많이 되게.. 광고 봐도 로드 되고
        var adRequest = AdRequest.Builder().build()
        RewardedAd.load(this,"ca-app-pub-8246055051187544/7596977895", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                rewardedAd = ad
            }
        })



    }

    private fun getCommentsFromFirestore() {

        val db = Firebase.firestore

        if(currentPageNumber == 0) {
            db.collection(intentSingerId)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(pageLimit)
                .get()
                .addOnSuccessListener { result ->
                    addCommentSuccessListener(result)
                }.addOnFailureListener { exception ->

                }
        } else {
            db.collection(intentSingerId)
                .orderBy("date", Query.Direction.DESCENDING)
                .startAfter(lastVisible) // 마지막으로 가져온 document 그래야더보기할떄 다음꺼부터 가져옴
                .limit(pageLimit)
                .get()
                .addOnSuccessListener { result ->
                    addCommentSuccessListener(result)
                }.addOnFailureListener { exception ->

                }
        }

    }

    private fun addCommentSuccessListener(result: QuerySnapshot) {
        if (result.documents.size < pageLimit) {
            llMore.visibility = GONE
        } else {
            lastVisible = result.documents[result.documents.size - 1]
        }
        for (document in result) {
            cmDataList.add(
                CmData(
                    username = document.data["username"] as String,
                    singerId = document.data["singerId"] as String,
                    say = document.data["say"] as String,
                    date = document.data["date"] as String,
                    commentId = document.id
                )
            )
        }
        cmViewAdapter.notifyDataSetChanged()

    }
//    private fun getCommentsFromFirebase() {
//
//        val items = findViewById<RecyclerView>(R.id.cm_rv)
//
//        val adapter_list = CmViewAdapter(cmDataList)
//
//        items.adapter = adapter_list
//        items.layoutManager = LinearLayoutManager(this)
//
//
//
//        // 댓글 가져와서 보여주는 부분
//        val database = Firebase.database
//        val myRef = database.getReference("comment")
//
//        myRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                cmDataList.clear()
//
//                for (cmData in snapshot.children) {
//                    if (cmData.getValue(CmData::class.java)!!.singerId == intentSingerId) {
//                        cmDataList.add(cmData.getValue(CmData::class.java)!!)
//
//                    }
//                }
//
//                adapter_list.notifyDataSetChanged()
//
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
//
//    }

    private fun showEditDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.alertid, null)
        val dialogText = dialogLayout.findViewById<EditText>(R.id.idInsert)

        builder.setView(dialogLayout)
            .setPositiveButton("확인") { dialogInterface, i ->
                val nickname = dialogText.text.toString()
                if(nickname == "") {
                    Toast.makeText(this,"닉네임을 입력해주세요!", Toast.LENGTH_LONG).show()
                } else {
                    val pref = getSharedPreferences("pref", 0)
                    val edit = pref.edit() //수정모드
                    edit.putString("Nickname", nickname) //첫번쨰는 키값, 2번은 실제 값
                    edit.apply() //값을 저장
                }
            }
//            .setNegativeButton("취소") { dialogInterface, i ->
//            }
            .show()

    }

    private fun getStringArrayPref(context: Context, key: String): ArrayList<String>? {
        val pref = context.getSharedPreferences("pref",0)
        val json = pref.getString(key, null)
        val commentIdList = ArrayList<String>()
        if (json != null) {
            try {
                val a = JSONArray(json)
                for (i in 0 until a.length()) {
                    val commentId = a.optString(i)
                    commentIdList.add(commentId)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return commentIdList
    }


}
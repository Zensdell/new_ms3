package com.kkk.new_ms3

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings



val datas = ArrayList<ProfileData>()
var likeDatas = ArrayList<ProfileData>()

class MainActivity : FragmentActivity() {

    val datas2 = mutableListOf<ProfileData>()

    lateinit var tabs: TabLayout
    lateinit var fragmentAll: FragmentAll
    lateinit var fragmentLike: FragmentLike
    lateinit var fragmentMySet: FragmentMySet

    var isShowingAlertDialog: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //메인액티비티 뜨자마자 데이터 가져오기
        dataFromFirestore()
    }

    private fun checkRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUpdateVersion = remoteConfig.getLong("updateVersionCode")
                    Log.d(ContentValues.TAG, "checkRemoteConfig: $firebaseUpdateVersion")

                    try {
                        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
                        val userVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            pInfo.longVersionCode
                        } else {
                            pInfo.versionCode.toLong()
                        }
                        if(firebaseUpdateVersion > userVersion) {
                            if(!isShowingAlertDialog) {
                                isShowingAlertDialog = true
                                AlertDialog.Builder(this)
                                    .setCancelable(false)
                                    .setTitle("업데이트 공지")
                                    .setMessage("정상적인 응원댓글을 위해 최신버젼으로 업데이트 해주세요.")
                                    .setPositiveButton("확인") { dialog, which ->

                                        val myUri =
                                            Uri.parse("market://details?id=com.kkk.new_ms3") // 플레이스토어 주소 대입.
                                        val myIntent = Intent(Intent.ACTION_VIEW, myUri)
                                        startActivity(myIntent)
                                        isShowingAlertDialog = false

                                    }
                                    .show()
                            }
                        } else {
                            isShowingAlertDialog = false
                        }

                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
    }

    private fun dataFromFirestore() {
        val pref = getSharedPreferences("pref",0)
        val db = Firebase.firestore
        db.collection("Singers")
            .get()
            .addOnSuccessListener { result ->

                datas.clear()
                likeDatas.clear()

                Log.d(ContentValues.TAG, "dataFromFirestore: Data READ??????")

                for (document in result) {
                    datas.add(
                        ProfileData(
                            img = document.data["imageUrl"] as String,
                            name = document.data["name"] as String,
                            stageUrl = document.data["stageUrl"] as String,
                            singerId = document.data["id"] as String,
                            infoUrl = document.data["infoUrl"] as String,
                            commentCount = document.data["commentCount"] as Long,
                            // ytUrl = document.data["ytUrl"] as String,
                        )
                    )

                    val singerId: String = document.data["id"] as String
                    val isLiked = pref.getBoolean(singerId, false)
                    if(isLiked) {
                        likeDatas.add(
                            ProfileData(
                                img = document.data["imageUrl"] as String,
                                name = document.data["name"] as String,
                                stageUrl = document.data["stageUrl"] as String,
                                singerId = document.data["id"] as String,
                                infoUrl = document.data["infoUrl"] as String,
                                commentCount = document.data["commentCount"] as Long,
                                //  ytUrl = document.data["ytUrl"] as String,
                            )
                        )
                    }

                }

                for (document in result) {
                    datas2.add(
                        ProfileData(
                            img = document.data["imageUrl"] as String,
                            name = document.data["name"] as String,
                            stageUrl = document.data["stageUrl"] as String,
                            singerId = document.data["id"] as String,
                            infoUrl = document.data["infoUrl"] as String,
                            commentCount = document.data["commentCount"] as Long,
                            // ytUrl = document.data["ytUrl"] as String,
                        )
                    )

                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                }

                datas2.sortByDescending { it.commentCount }
                datas.add(0,datas2[8])
                datas.add(0,datas2[7])
                datas.add(0,datas2[6])
                datas.add(0,datas2[5])
                datas.add(0,datas2[4])
                datas.add(0,datas2[3])
                datas.add(0,datas2[2])
                datas.add(0,datas2[1])
                datas.add(0,datas2[0])

                fragmentAll = FragmentAll()
                fragmentLike = FragmentLike()
                fragmentMySet = FragmentMySet()

                supportFragmentManager.beginTransaction().add(R.id.container, fragmentAll).commit()
                tabs = findViewById(R.id.tabs)
//                tabs.addTab(tabs.newTab().setText("전체 가수"))
//                tabs.addTab(tabs.newTab().setText("내가 찜한 가수"))
                tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                    override fun onTabSelected(tab: TabLayout.Tab) {

                        val position = tab.position
                        var selected: Fragment? = null

                        // 탭이 선택되면 나타내줄 프래그먼트를 정하는곳

                        if (position == 0) selected = fragmentAll
                        else if (position == 1) selected = fragmentLike
                        else if (position == 2)  selected = fragmentMySet

                        supportFragmentManager.beginTransaction().replace(R.id.container, selected!!).commit()

                    }

                    override fun onTabUnselected(tab: TabLayout.Tab) {}
                    override fun onTabReselected(tab: TabLayout.Tab) {}

                })


            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "인터넷 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
            }
    }


}
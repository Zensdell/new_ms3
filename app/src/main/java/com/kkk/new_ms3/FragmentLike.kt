package com.kkk.new_ms3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


class FragmentLike  : Fragment() {

    private var mInterstitialAd : InterstitialAd?=null

    lateinit var rvAdapter: RVLikeAdapter
    private val TAG = "FragmentLike"

    override fun onResume() {
        Log.d(TAG, "onResume: ")
        rvAdapter.datas = likeDatas
        likeDatas.forEach{
            Log.d(TAG, "onResume: "+it.singerId)
        }
        rvAdapter.notifyDataSetChanged()
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 여기가 메인액티비티에서 온크리에이트랑 비슷한 역할을하는함수야
        // 프래그먼트라 이름이 온크리에이트뷰임

        MobileAds.initialize(requireContext()) {}
        Log.d("애드몹전면광고","로드하나?")


        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(requireContext(),"ca-app-pub-8246055051187544/8016910252", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })




        val view: View = inflater.inflate(R.layout.fragment_like, container, false) as ViewGroup
        // view 를 inflate 로 받으면 view 안에 이제 context 가 들어가는것임 ! (원래는 Activity 안에 있던것을  Fragment 로 옮기면서 이렇게 변경됨)


        val recyclerView = view.findViewById<RecyclerView>(R.id.like_rv)
        rvAdapter = RVLikeAdapter(view.context)
        rvAdapter.datas = likeDatas

        recyclerView.adapter = rvAdapter

        rvAdapter.itemClick=object : RVLikeAdapter.ItemClick{
            override fun onClick(view: View, position: Int) {

                val intent = Intent(view.context, ViewActivity::class.java)
                intent.putExtra("img",likeDatas[position].img)
                intent.putExtra("stageUrl", likeDatas[position].stageUrl)
                intent.putExtra("singerId", likeDatas[position].singerId)
                intent.putExtra("infoUrl",likeDatas[position].infoUrl)
                intent.putExtra("commentCount",likeDatas[position].commentCount)
                intent.putExtra("name",datas[position].name)

                startActivity(intent)

                if (mInterstitialAd != null) {
                    Log.d("애드몹전면광고","보여준다")
                    mInterstitialAd?.show(activity!!)
                } else {
                    //   Toast.makeText(context,"광고 로드 실패", Toast.LENGTH_SHORT).show()//광고를 불러오지 못했을때
                }
            }
        }
        recyclerView.layoutManager = GridLayoutManager(view.context,3)

        return view
    }

}
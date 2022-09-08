package com.tunahan.retrofitkotlin.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tunahan.retrofitkotlin.adapter.CryptoAdapter
import com.tunahan.retrofitkotlin.databinding.ActivityMainBinding
import com.tunahan.retrofitkotlin.model.CryptoModel
import com.tunahan.retrofitkotlin.service.CryptoAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(),CryptoAdapter.Listener {

    private lateinit var binding: ActivityMainBinding

    private val BASE_URL = "https://raw.githubusercontent.com/"
    private var cryptoModels:ArrayList<CryptoModel>?=null
    private var cryptoAdapter:CryptoAdapter?=null

    private var compositeDisposable: CompositeDisposable?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//https://raw.githubusercontent.com/atilsamancioglu/K21-JSONDataSet/master/crypto.json


        compositeDisposable = CompositeDisposable()

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        loadData()



    }

    private fun loadData(){

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CryptoAPI::class.java)


        compositeDisposable?.add(retrofit.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleResponse)
        )


/*

val service = retrofit.create(CryptoAPI::class.java)
val call = service.getData()


    call.enqueue(object: Callback<List<CryptoModel>>{

        override fun onResponse(
            call: Call<List<CryptoModel>>,
            response: Response<List<CryptoModel>>
        ) {
            if(response.isSuccessful){
                response.body().let {
                    cryptoList = ArrayList(it)

                    cryptoList?.let {

                        cryptoAdapter = CryptoAdapter(it,this@MainActivity)
                        binding.recyclerView.adapter = cryptoAdapter

                    }

                    for (crp:CryptoModel in cist!!){

                        println(crp.currency)
                        println(crp.price)
                    }



                }
            }

        }

        override fun onFailure(call: Call<List<CryptoModel>>, t: Throwable) {
           t.printStackTrace()
        }

    } )
    */

}

    private fun handleResponse(cryptoList: List<CryptoModel>){
        cryptoModels = ArrayList(cryptoList)

        cryptoModels?.let {

            cryptoAdapter = CryptoAdapter(it,this@MainActivity)
            binding.recyclerView.adapter = cryptoAdapter

        }

    }

override fun onItemClick(cryptoModel: CryptoModel) {
    Toast.makeText(this@MainActivity,"Clicked: ${cryptoModel.currency}",Toast.LENGTH_LONG).show()
}

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }

}
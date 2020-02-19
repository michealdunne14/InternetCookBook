package com.example.internetcookbook.network

import android.content.Context
import android.graphics.Bitmap
import com.example.internetcookbook.helper.exists
import com.example.internetcookbook.helper.read
import com.example.internetcookbook.helper.write
import com.example.internetcookbook.models.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okio.IOException
import kotlin.collections.ArrayList


class InformationStore(val context: Context, val internetConnection: Boolean) {
    var client = OkHttpClient()
    var userMaster = UserMasterModel()
    var imageArrayList = ArrayList<Bitmap>()
    var postData = ArrayList<DataModel>()
    var cupboardData = ArrayList<FoodMasterModel>()
    var basketData = ArrayList<FoodMasterModel>()
    var basket = ArrayList<FoodMasterModel>()
    var followingData = ArrayList<UserMasterModel>()
    var userLocalStore = mutableListOf<UserMasterModel>()
    lateinit var emailSearchArray: Array<UserModel>


    val JSON_FILE = "InformationStore.json"
    val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
    val listType = object : TypeToken<ArrayList<UserMasterModel>>() {}.type

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(userLocalStore, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        userLocalStore = Gson().fromJson(jsonString, listType)
    }

//    fun emailSearch(userModel: UserModel) {
//        val request = Request.Builder()
//            .url("http://52.51.34.156:3000/user/email/${userModel.email}")
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                response.use {
//                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//                    val body = response.body!!.string()
//                    val gsonBuilder = GsonBuilder()
//                    val gson = gsonBuilder.create()
//
//                    val emailSearch: Array<UserModel> =
//                        gson.fromJson<Array<UserModel>>(body, Array<UserModel>::class.java)
//                    emailSearchArray = emailSearch
//                }
//            }
//        })
//    }

    fun findEmail(userModel: UserModel): UserMasterModel? {
        if (internetConnection) {
            lateinit var emailSearch: UserMasterModel
            val request = Request.Builder()
                .url("http://52.51.34.156:3000/user/email/${userModel.email}")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()

                emailSearch = gson.fromJson(body,UserMasterModel::class.java)
            }
            if (!emailSearch.user.email.isEmpty()) {
                userLocalStore.clear()
                userMaster = emailSearch
                userMaster.user.loggedIn = true
                userLocalStore.add(userMaster)
                serialize()
                return userMaster
            } else {
                return null
            }
        } else {
            return null
        }
    }

    fun getCurrentUser(): UserMasterModel {
        if (userLocalStore.isNotEmpty()) {
            userMaster = userLocalStore[0]
        }
        return userMaster
    }

    fun createUser(userModel: UserModel): String? {
//        userMaster.user = userModel
        val user = findEmail(userModel)
//        if (user == null) {
            val formBody: RequestBody = FormBody.Builder()
                .add("username", userModel.username)
                .add("password", userModel.password)
                .add("name", userModel.name)
                .add("email", userModel.email)
                .add("signupdate", userModel.signupdate).build()

            val request: Request = Request.Builder()
                .url("http://52.51.34.156:3000/user/create")
                .post(formBody)
                .build()

            client.newCall(request).execute().use { response -> return response.body!!.toString() }
//        }else{
//            return null
//        }
    }

    fun updateUserInfo(userModel: UserModel): UserMasterModel? {
        if (internetConnection) {
            lateinit var emailSearch: UserMasterModel
            val request = Request.Builder()
                .url("http://52.51.34.156:3000/user/id/${userModel.oid}")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val body = response.body!!.string()
                val gsonBuilder = GsonBuilder()
                val gson = gsonBuilder.create()

                emailSearch = gson.fromJson(body,UserMasterModel::class.java)
            }
            if (!emailSearch.user.email.isEmpty()) {
                userLocalStore.clear()
                userMaster = emailSearch
                userMaster.user.loggedIn = true
                userLocalStore.add(userMaster)
                serialize()
                return userMaster
            } else {
                return null
            }
        } else {
            return null
        }
    }

    fun createFood(foodModel: FoodModel): String {
        val formBody: RequestBody = FormBody.Builder()
            .add("name", foodModel.name)
            .add("price", foodModel.price.toString())
            .add("shop", foodModel.shop).build()

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/food/create")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response -> return response.body!!.toString() }
    }

    fun findShop(shop: String): String? {
        lateinit var foodModel: Array<FoodModel>
        val request = Request.Builder()
            .url("http://52.51.34.156:3000/food/shop/${shop}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()

            foodModel = gson.fromJson<Array<FoodModel>>(body, Array<FoodModel>::class.java)
        }
        return if (foodModel.isNotEmpty()) {
            foodModel[0].shop
        } else {
            null
        }
    }

    fun findItem(item: String): FoodMasterModel? {
        val request = Request.Builder()
            .url("http://52.51.34.156:3000/food/name/${item}")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            val body = response.body!!.string()
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()
            return gson.fromJson(body, FoodMasterModel::class.java)
        }
    }

    fun userCreated() {
        userLocalStore.add(userMaster)
        serialize()
    }

    fun getHomeData(): ArrayList<DataModel> {
        return postData
    }

    fun getPostData(){
        postData.clear()
        lateinit var dataArray: DataModel
        if (internetConnection) {
            for (post in userMaster.user.posts) {
                val request = Request.Builder()
                    .url("http://52.51.34.156:3000/post/id/${post?.postoid}")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    val body = response.body!!.string()
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()
                    dataArray = gson.fromJson(body, DataModel::class.java)
                    postData.add(dataArray)
                }
            }
            for (following in followingData){
                for(posts in following.user.posts) {
                    val request = Request.Builder()
                        .url("http://52.51.34.156:3000/post/id/${posts?.postoid}")
                        .build()

                    client . newCall (request).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")


                        val body = response.body!!.string()
                        val gsonBuilder = GsonBuilder()
                        val gson = gsonBuilder.create()
                        dataArray = gson.fromJson(body, DataModel::class.java)

                        postData.add(dataArray)
                    }
                }
            }
        }
    }

    fun getCupboardData(){
        cupboardData.clear()
        lateinit var dataArray: FoodMasterModel
        if (internetConnection) {
            for (cupboard in userMaster.user.cupboard) {
                val request = Request.Builder()
                    .url("http://52.51.34.156:3000/food/foodId/${cupboard?.cupboardoid}")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    val body = response.body!!.string()
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()

                    dataArray = gson.fromJson(body, FoodMasterModel::class.java)
                    cupboardData.add(dataArray)
                }
            }
        }
    }


    fun getBasketData(){
        basketData.clear()
        lateinit var dataArray: FoodMasterModel
        if (internetConnection) {
            for (basket in userMaster.user.basket) {
                val request = Request.Builder()
                    .url("http://52.51.34.156:3000/food/foodId/${basket?.basketoid}")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val body = response.body!!.string()
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()

                    dataArray = gson.fromJson(body, FoodMasterModel::class.java)
                    basketData.add(dataArray)
                }
            }
        }
    }

    fun getFollowingData(){
        followingData.clear()
        lateinit var dataArray: UserMasterModel
        if (internetConnection) {
            for (following in userMaster.user.following) {
                val request = Request.Builder()
                    .url("http://52.51.34.156:3000/user/id/${following?.followingoid}")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    val body = response.body!!.string()
                    val gsonBuilder = GsonBuilder()
                    val gson = gsonBuilder.create()

                    dataArray = gson.fromJson(body, UserMasterModel::class.java)
                    followingData.add(dataArray)
                }
            }
        }
    }

    fun findFollowingData(): ArrayList<UserMasterModel> {
        return followingData
    }

    fun findBasketData(): ArrayList<FoodMasterModel> {
        return basketData
    }

    fun findCupboardData(): ArrayList<FoodMasterModel> {
        return cupboardData
    }

    fun uploadImages(oid: String, listofImages: ArrayList<String>) {
        //        val file = File(listofImages[0])
//        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart("imageFiles", listofImages[0], RequestBody.create(MEDIA_TYPE_PNG, file))
//            .build()
//
//        val request = Request.Builder()
//            .url(" http://52.51.34.156:3000/post/upload/${oid}")
//            .post(requestBody)
//            .build()
//
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//            println(response.body!!.string())
//        }

    }


    fun createPost(postModel: PostModel): String? {
        val formBody: RequestBody = FormBody.Builder()
            .add("title", postModel.title)
            .add("description", postModel.description)
            .add("useroid", userMaster.user.oid).build()

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/post/create")
            .post(formBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            return response.body!!.string()
        }
    }


    fun putPostToUser(postModel: PostModel): String? {
        val formBody: RequestBody = FormBody.Builder()
            .add("title", postModel.title)
            .add("description", postModel.description)
            .add("image", "randomimage")
            .add("useroid", userMaster.user.oid).build()

        val request: Request = Request.Builder()
            .url("http://52.51.34.156:3000/post/create")
            .put(formBody)
            .build()

        client.newCall(request).execute().use { response -> return response.body!!.toString() }
    }

    fun logoutUser() {
        userLocalStore.clear()
        serialize()
    }
}
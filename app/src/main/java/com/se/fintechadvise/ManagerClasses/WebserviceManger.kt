    package com.se.fintechadvise.ManagerClasses

    import android.content.ContentValues
    import android.content.Context
    import android.util.Base64
    import android.util.Log
    import com.android.volley.AuthFailureError
    import com.android.volley.DefaultRetryPolicy
    import com.android.volley.Request
    import com.android.volley.Response
    import com.android.volley.toolbox.JsonArrayRequest
    import com.android.volley.toolbox.JsonObjectRequest
    import com.android.volley.toolbox.StringRequest
    import com.android.volley.toolbox.Volley
    import com.se.fintechadvise.DataClasses.Investment
    import com.se.fintechadvise.DataClasses.InvestmentPerformance
    import com.se.fintechadvise.DataClasses.InvestmentType
    import com.se.fintechadvise.DataClasses.User
    import com.se.fintechadvise.HelperClasses.CustomToastMaker
    import com.se.fintechadvise.HelperClasses.SecurityHelper
    import org.json.JSONArray
    import org.json.JSONObject

    object WebserviceManger {

        private var instance: WebserviceManger? = null

        private val BASE_URL = "http://192.168.1.5:5000/"

        @JvmStatic
        fun getInstance(): WebserviceManger {
            if (instance == null) {
                instance = WebserviceManger
            }
            return instance!!
        }

        //run a basic function to hit the base_url on the /add_table endpoint

        fun testRequest(context: Context)    {
            val queue = Volley.newRequestQueue(context)
            val url = BASE_URL + "add_table"
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    Log.d(ContentValues.TAG, "Response: $response")

                },
                { error ->
                    Log.e(ContentValues.TAG, "Error: $error")
                }
            )
            queue.add(stringRequest)
        }


        fun postRequest(
            endpoint: String,
            params: JSONObject, // Change this to JSONObject
            context: Context,
            successHandler: (String) -> Unit,
            errorHandler: (String?) -> Unit
        ) {
            val queue = Volley.newRequestQueue(context)

            val url = BASE_URL + endpoint

            val jsonObjectRequest = object : JsonObjectRequest(
                Method.POST, url, params,
                Response.Listener { response ->
                    successHandler(response.toString())
                },
                Response.ErrorListener { error ->
                    Log.e(ContentValues.TAG, "Error: $error")
                    errorHandler(error.toString())
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
            jsonObjectRequest.setRetryPolicy(
                DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )
            queue.add(jsonObjectRequest)
        }

        fun jsonArrayGetRequest(
            endpoint: String,
            params: JSONObject,
            context: Context,
            successHandler: (JSONArray) -> Unit,
            errorHandler: (String?) -> Unit
        ) {
            val queue = Volley.newRequestQueue(context)

            val url = BASE_URL + endpoint

            val jsonObjectRequest = object : JsonArrayRequest(
                Method.POST, url, JSONArray().put(params),
                Response.Listener { response ->
                    successHandler(response)
                },
                Response.ErrorListener { error ->
                    Log.e(ContentValues.TAG, "Error: $error")
                    errorHandler(error.toString())
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
            jsonObjectRequest.setRetryPolicy(
                DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )
            queue.add(jsonObjectRequest)
        }


        fun getRequest(
            endpoint: String,
            params: JSONObject,
            context: Context,
            successHandler: (String) -> Unit,
            errorHandler: (String?) -> Unit
        ) {
            val queue = Volley.newRequestQueue(context)

            val url = BASE_URL + endpoint

            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, url, params,
                Response.Listener { response ->
                    successHandler(response.toString())
                },
                Response.ErrorListener { error ->
                    Log.e(ContentValues.TAG, "Error: $error")
                    errorHandler(error.toString())
                }

            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }
            jsonObjectRequest.setRetryPolicy(
                DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
            )
            queue.add(jsonObjectRequest)
        }
        fun saveUserToWebService(user: User, context: Context, Callback:(Boolean)->Unit) {
            val params = JSONObject()
            params.put("id", user.id)
            params.put("name", user.name)
            params.put("email", user.email)
            params.put("country", user.country)
            params.put("phone", user.phone)
            params.put("password", user.password)
            params.put("occupation", user.occupation)
            params.put("age", user.age)
            params.put("income", user.income)
            params.put("riskTolerance", user.riskTolerance)

            // Generate the encryption key and IV and store them in SharedPreferences
            val (key, iv1) = SecurityHelper.generateKeyAndStoreInSharedPreferences(context)

            // Check if the key or IV is null
            if (key == null || iv1 == null) {
                Log.e(ContentValues.TAG, "Error: Key or IV is null")
                return
            }

            val (iv, encryptedPassword) = SecurityHelper.encrypt(user.password.toByteArray(), key,iv1)
            val encryptedPasswordString = Base64.encodeToString(encryptedPassword, Base64.DEFAULT)

            params.put("password", encryptedPasswordString)
            params.put("phone", user.phone)

            postRequest("signup", params, context,
                successHandler = { response ->
                    CustomToastMaker().showToast(context, "Sign up successful")
                    Log.d(ContentValues.TAG, "Response: $response")
                    Callback(true)
                    return@postRequest
                },
                errorHandler = { error ->
                    CustomToastMaker().showToast(context, "Sign up failed")
                    Log.e(ContentValues.TAG, "Error Sign up: $error")
                    Callback(false)
                    return@postRequest
                }
            )
        }

        fun loginUser(email: String, password: String, context: Context, Callback:(Boolean, User?)->Unit) {
            val params = JSONObject()
            params.put("email", email)
            Log.d("Email", email)
            // we need to encrypt the password before sending it to the server

            val (key1,iv1) = SecurityHelper.getKeyAndIvFromSharedPreferences(context)

            // Check if the key is null

            if (key1 == null) {
                Log.e(ContentValues.TAG, "Error: Key is null")
                Callback(false, null)
                return
            }

            val encryptedResult = iv1?.let {
                SecurityHelper.encrypt(password.toByteArray(), key1, it)
            }

            val iv = encryptedResult?.first
            val encryptedPassword1 = encryptedResult?.second

            val encryptedPasswordString2 = Base64.encodeToString(encryptedPassword1, Base64.DEFAULT)

            params.put("password", encryptedPasswordString2)
            Log.d("Encrypted Password", encryptedPasswordString2)
            Log.d("Password", password)

            postRequest("login", params, context,
                successHandler = { response ->
                    val jsonResponse = JSONObject(response)
                    val encryptedPasswordString = jsonResponse.getString("password")
                    val encryptedPassword = Base64.decode(encryptedPasswordString, Base64.DEFAULT)

                    // Get the key from SharedPreferences
                    // Get the key and IV from SharedPreferences
                    val (key, iv) = SecurityHelper.getKeyAndIvFromSharedPreferences(context)

                    // Check if the key or IV is null
                    if (key == null || iv == null) {
                        Log.e(ContentValues.TAG, "Error: Key or IV is null")
                        Callback(false, null)
                        return@postRequest
                    }

                    Log.d("Encrypted Password", encryptedPasswordString)

                    val decryptedPassword = SecurityHelper.decrypt(encryptedPassword, iv, key)
                    val decryptedPasswordString = String(decryptedPassword)

                    Log.d("Decrypted Password", decryptedPasswordString)

                    var user = User()
                    user.id = jsonResponse.getString("id")
                    user.name = jsonResponse.getString("name")
                    user.email = jsonResponse.getString("email")
                    user.country = jsonResponse.getString("country")
                    user.password = decryptedPasswordString
                    user.phone = jsonResponse.getString("phone")
                    user.occupation = jsonResponse.getString("occupation")
                    user.age = jsonResponse.getString("age")
                    user.income = jsonResponse.getString("income")
                    user.riskTolerance = jsonResponse.getString("riskTolerance")

                    Log.d("Users","${user.id}, ${user.name}, ${user.email}, ${user.country}, ${user.phone}")


                    if (decryptedPasswordString == password) {
                        CustomToastMaker().showToast(context, "Login successful")
                        Log.d(ContentValues.TAG, "Response: $response")
                        Callback(true, user)
                    } else {
                        CustomToastMaker().showToast(context, "Login failed")
                        Log.e(ContentValues.TAG, "Error Login: Passwords do not match")
                        Callback(false, null)
                    }
                },
                errorHandler = { error ->
                    CustomToastMaker().showToast(context, "Login failed")
                    Log.e(ContentValues.TAG, "Error Login: $error")
                    Callback(false, null)
                }
            )
        }

        fun getInvestments(context: Context, callback: (List<Investment>?, String?) -> Unit) {
            val queue = Volley.newRequestQueue(context)

            val url = BASE_URL + "get_investments" // replace "investments" with the actual endpoint

            val jsonArrayRequest = object : JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->
                    Log.d("getInvestments", "Response: $response")
                    val investments = ArrayList<Investment>()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val datesJsonArray = item.getJSONArray("dates")
                        val valuesJsonArray = item.getJSONArray("values")

                        val historicalPerformance = mutableListOf<InvestmentPerformance>()
                        for (j in 0 until datesJsonArray.length()) {
                            val date = datesJsonArray.getString(j)
                            val value = valuesJsonArray.getDouble(j)
                            historicalPerformance.add(InvestmentPerformance(date, value))
                        }

                        val investment = Investment(
                            id = item.getString("id"),
                            name = item.getString("name"),
                            allocation = item.optDouble("allocation", 0.0),
                            type = InvestmentType.valueOf(item.getString("type")),
                            investmentImageUrl = BASE_URL + item.getString("investmentImageUrl"),
                            currentValue = item.getDouble("currentValue"),
                            historicalPerformance = historicalPerformance,
                            performanceIndex = item.getDouble("performanceIndex")
                        )
                        investments.add(investment)
                        Log.d("Investment", "Investment: $investment")
                    }
                    callback(investments, null)
                },
                { error ->
                    Log.e("getInvestments", "Error: $error")
                    callback(null, error.toString())
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            queue.add(jsonArrayRequest)
        }



        fun addUserInvestment(context: Context, userId: String, investmentId: String, allocation: Double) {
            val queue = Volley.newRequestQueue(context)

            val url = BASE_URL + "add_users_investments"

            val params = JSONObject()
            params.put("user_id", userId)
            params.put("investment_id", investmentId)
            params.put("allocation", allocation)

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, url, params,
                { response ->
                    CustomToastMaker().showToast(context, "Investment added successfully")
                },
                { error ->
                   // CustomToastMaker().showToast(context, "Failed to add investment")
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        }

        fun getUserInvestments(context: Context, userId: String, callback: (List<String>?, String?) -> Unit) {
            val queue = Volley.newRequestQueue(context)

            val url = BASE_URL + "get_users_investments"

            val params = JSONObject()
            params.put("user_id", userId)

            val jsonArrayRequest = object : JsonArrayRequest(
                Request.Method.POST, url, JSONArray().put(params),
                { response ->
                    val investmentIds = ArrayList<String>()
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val investmentId = item.getString("investment_id")
                        investmentIds.add(investmentId)
                    }
                    callback(investmentIds, null)
                },
                { error ->
                    callback(null, error.toString())
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    return headers
                }
            }

            queue.add(jsonArrayRequest)
        }





    }
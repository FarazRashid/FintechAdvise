    package com.se.fintechadvise.ManagerClasses

    import android.content.ContentValues
    import android.content.Context
    import android.util.Base64
    import android.util.Log
    import android.widget.Toast
    import com.android.volley.AuthFailureError
    import com.android.volley.DefaultRetryPolicy
    import com.android.volley.Request
    import com.android.volley.Response
    import com.android.volley.toolbox.JsonArrayRequest
    import com.android.volley.toolbox.JsonObjectRequest
    import com.android.volley.toolbox.StringRequest
    import com.android.volley.toolbox.Volley
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

    }
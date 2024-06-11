package com.example.weathernews

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.weathernews.ui.theme.WeathernewsTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

class MainActivity : ComponentActivity() {
    lateinit var database: weatherDatabase
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeathernewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    database = Room.databaseBuilder(applicationContext,
                        weatherDatabase::class.java,
                        "Weather").build()
                    Screen(database=database)
                }
            }
        }
    }
}

@Composable
fun Screen(database: weatherDatabase){
    val customHeadingStyle = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 50.sp,
        color = Color.Red
    )
    val customResultStyle = androidx.compose.ui.text.TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp,
        color = Color.Black
    )

    var getDate by remember { mutableStateOf("") }
    var getCity by remember { mutableStateOf("") }
    var maxResult by remember { mutableStateOf("") }
    var minResult by remember { mutableStateOf("") }
    var ifWeatherCheck by remember { mutableStateOf(false) }
    var temp by remember { mutableStateOf("") }
    val greenColor = Color(0xFF006400)

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = "Weather App",style= customHeadingStyle)
            Spacer(modifier = Modifier.height(200.dp))
            OutlinedTextField(value = getCity, onValueChange = {getCity=it}, label = {Text(text="Enter the City")})
            Spacer(modifier = Modifier.height(15.dp))
            OutlinedTextField(value = getDate, onValueChange = {getDate=it}, label = {Text(text="Enter the Date : YYYY-MM-DD")})
            Spacer(modifier = Modifier.height(15.dp))
            Button(onClick = {
                ifWeatherCheck = true

                GlobalScope.launch {
                    val output = database.WeatherDao().getResult(getCity,getDate)
                    maxResult = output?.MaxTemp ?: "N/A"
                    minResult = output?.MinTemp ?: "N/A"
                }
                val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl("https://weather.visualcrossing.com/").build().create(ApiInterface::class.java)
                val response = retrofit.getAPI(getCity,getDate,"Your_Api_Key","current")
                response.enqueue(object : Callback<WeatherApp>{
                    override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>){
                        val getResponse  = response.body()
                        if(response.isSuccessful && getResponse !=null){
                            temp = getResponse.days[0].tempmax.toString()
                            maxResult = ((((getResponse.days[0].tempmax)-32)*5)/9).let { "%.2f".format(it).toDouble() }.toString()
                            minResult = ((((getResponse.days[0].tempmin)-32)*5)/9).let { "%.2f".format(it).toDouble() }.toString()
                            GlobalScope.launch {
                                database.WeatherDao().insert(WeatherEntity(0, getCity, getDate,maxResult,minResult))
                            }
                        }
                    }
                    override fun onFailure(call: Call<WeatherApp>,t: Throwable){
                    }
                })
            },colors = ButtonDefaults.buttonColors(greenColor)) {
                Icon(imageVector = Icons.Default.Place, contentDescription = "stop", tint = Color.White)
                Text(text = "Weather")
            }
            Spacer(modifier = Modifier.height(15.dp))
            if(ifWeatherCheck){
                Row{
                    Icon(imageVector = Icons.Default.Place, contentDescription = "stop", tint = Color.Black)
                    Text(text = "Max Temp in ${getCity} on ${getDate} : ${maxResult}°C",style=customResultStyle)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row{
                    Icon(imageVector = Icons.Default.Place, contentDescription = "stop", tint = Color.Black)
                    Text(text = "Min Temp in ${getCity} on ${getDate} : ${minResult}°C",style=customResultStyle)
                }
            }
        }
    }
}
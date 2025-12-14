package com.example.test
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.test.R // Ojo: R es la clase que contiene todos tus recursos, incl. drawables
import android.os.Bundle
import android.util.Log
import android.widget.Toast // Importar Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Importar Contexto
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.test.ui.theme.TESTTheme
import kotlinx.coroutines.launch // Importar corrutinas
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName // Asegúrate de tener GSON
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.text.font.FontWeight



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TESTTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudentForm(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun StudentForm(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var codigo by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var esActivo by remember { mutableStateOf(true) }
    var isSending by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // --- FONDO ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFCFB))
    )
    {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // LOGO
            Image(
                painter = painterResource(id = R.drawable.ic_student_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(170.dp)
                    .padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // TITULO
            Text(
                text = "REGISTRO DE ESTUDIANTES",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),

                color =  Color(0xFF000000),
                modifier = Modifier.padding(bottom = 30.dp)
            )

            // --- RECUADRO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFAFAFA)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {

                    // TEXTFIELDS
                    OutlinedTextField(
                        value = codigo,
                        onValueChange = { codigo = it },
                        label = { Text("Ingrese su DNI") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null)
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = nombres,
                        onValueChange = { nombres = it },
                        label = { Text("Nombres y Apellidos") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = edad,
                        onValueChange = { if (it.all { char -> char.isDigit() }) edad = it },
                        label = { Text("Ingrese su Edad") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        leadingIcon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null)
                        },
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = carrera,
                        onValueChange = { carrera = it },
                        label = { Text("Programa de Estudios") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        leadingIcon = {
                            Icon(Icons.Default.School, contentDescription = null)
                        },
                        singleLine = true
                    )

                    // SWITCH
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "¿Estudiante activo?",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Switch(
                            checked = esActivo,
                            onCheckedChange = { esActivo = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF040005),   // Color del "botón"
                                checkedTrackColor = Color(0xFFE5E5E5),   // Color del fondo d

                            )
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTÓN  ---
            Button(
                onClick = {
                    val edadInt = edad.toIntOrNull()
                    if (edadInt == null) {
                        Toast.makeText(context, "Ingrese su edad plis", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isSending = true

                    // OPERA A CONECTAR CON EL API
                    scope.launch {
                        try {
                            val nuevoEstudiante = EstudianteRequest(
                                codigoEstudiante = codigo,
                                nombres = nombres,
                                edad = edadInt,
                                carrera = carrera,
                                activo = esActivo
                            )
                            // ENVIA A MI SERVIDOR MONGOGDB

                            val response = RetrofitClient.api.registrarEstudiante(nuevoEstudiante)

                            if (response.isSuccessful) {
                                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_LONG).show()
                                codigo = ""
                                nombres = ""
                                edad = ""
                                carrera = ""
                            } else {
                                Toast.makeText(context, "Error ${response.code()}", Toast.LENGTH_LONG).show()
                            }
                            //POR SI FALLA EL INTERNET O EL API
                        } catch (e: Exception) {
                            Toast.makeText(context, "Fallo: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isSending = false
                        }
                    }

                },
               //DISENO BOTON
                enabled = !isSending,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF000000), contentColor = Color.White)

            )


            {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Registrar Estudiante",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = " MANEJO DE DATOS - GRUPO 2",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

    }

}


package com.example.listacompras

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listacompras.db.AppDatabase
import com.example.listacompras.db.Compra
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// La actividad para agregar un nuevo producto a la lista de compras
class AgregarCompraActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el contenido de la actividad utilizando Jetpack Compose
        setContent {
            AgregarCompraUI()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarCompraUI() {
    // Obtiene el contexto actual
    val contexto = LocalContext.current
    // Alcance de la corrutina para operaciones asincrónicas
    val alcanceCorrutina = rememberCoroutineScope()
    // Estado mutable para el nombre del producto
    var nombre by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            // Barra superior con título y botón de navegación hacia atrás
            TopAppBar(
                title = { Text(stringResource(R.string.title_agregar_producto)) },
                navigationIcon = {
                    IconButton(onClick = {
                        // Finaliza la actividad cuando se presiona el botón de retroceso
                        (contexto as ComponentActivity).finish()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.button_volver))
                    }
                }
            )
        },
        content = { paddingValues ->
            // Contenido principal de la pantalla
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de carrito de compra
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = stringResource(R.string.icon_agregar),
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Campo de texto para ingresar el nombre del producto
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(stringResource(R.string.button_agregar)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Botón para agregar el producto a la base de datos
                Button(
                    onClick = {
                        // Operación asincrónica para insertar el producto en la base de datos
                        alcanceCorrutina.launch(Dispatchers.IO) {
                            val dao = AppDatabase.getInstance(contexto).compraDao()
                            // Inserta el nuevo producto
                            dao.insert(Compra(nombre = nombre))
                            // Finaliza la actividad y vuelve a la pantalla principal
                            withContext(Dispatchers.Main) {
                                (contexto as ComponentActivity).finish()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.button_agregar),
                        fontSize = 20.sp,
                        )
                }
            }
        }
    )
}

package com.example.listacompras

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listacompras.db.AppDatabase
import com.example.listacompras.db.Compra
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// La actividad principal que muestra la lista de compras
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el contenido de la actividad utilizando Jetpack Compose
        setContent {
            ListaComprasApp()
        }
    }

    override fun onResume() {
        super.onResume()
        // Actualiza el valor para indicar que se necesita recargar la lista
        shouldReload = true
    }

    companion object {
        // Estado compartido para indicar si la lista de compras debe recargarse
        var shouldReload by mutableStateOf(false)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaComprasApp() {
    // Estado mutable que contiene la lista de compras
    var compras by remember { mutableStateOf(emptyList<Compra>()) }
    // Obtiene el contexto actual
    val contexto = LocalContext.current
    val alcanceCorrutina = rememberCoroutineScope()

    // Efecto que se ejecuta cuando shouldReload cambia
    LaunchedEffect(MainActivity.shouldReload) {
        if (MainActivity.shouldReload) {
            alcanceCorrutina.launch(Dispatchers.IO) {
                val dao = AppDatabase.getInstance(contexto).compraDao()
                val comprasList = dao.getAll()
                withContext(Dispatchers.Main) {
                    compras = comprasList
                    MainActivity.shouldReload = false // Reinicia los valores despues de cargar datos
                }
            }
        }
    }

    Scaffold(
        topBar = {
            // Barra superior con el título de la pantalla
            TopAppBar(
                title = { Text("Lista de Compras") }
            )
        },
        floatingActionButton = {
            // Botón flotante para agregar un nuevo producto
            FloatingActionButton(
                onClick = {
                    // Inicia la actividad AgregarCompraActivity
                    contexto.startActivity(Intent(contexto, AgregarCompraActivity::class.java))
                },
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 16.dp
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        content = { paddingValues ->
            // Muestra un mensaje si la lista de compras está vacía
            if (compras.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No hay productos que mostrar",
                        fontSize = 20.sp,
                    )
                }
            } else {
                // Muestra la lista de compras
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(compras) { compra ->
                        // Composable para cada ítem de la lista de compras
                        CompraItem(compra) {
                            // Recarga la lista de compras cuando se actualiza un ítem
                            alcanceCorrutina.launch(Dispatchers.IO) {
                                val dao = AppDatabase.getInstance(contexto).compraDao()
                                val comprasList = dao.getAll()
                                withContext(Dispatchers.Main) {
                                    compras = comprasList
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CompraItem(compra: Compra, onSave: () -> Unit) {
    // Obtiene el contexto actual
    val contexto = LocalContext.current
    // Alcance de la corrutina para operaciones asincrónicas
    val alcanceCorrutina = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        // Muestra un icono dependiendo del estado de "comprado" del producto
        if (compra.comprado) {
            Icon(
                Icons.Filled.Check,
                contentDescription = "Comprado",
                tint = Color.Green,
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO) {
                        val dao = AppDatabase.getInstance(contexto).compraDao()
                        compra.comprado = false
                        dao.update(compra)
                        withContext(Dispatchers.Main) {
                            onSave()
                        }
                    }
                }
            )
        } else {
            Icon(
                Icons.Filled.Warning,
                contentDescription = "No comprado",
                tint = Color.Red,
                modifier = Modifier.clickable {
                    alcanceCorrutina.launch(Dispatchers.IO) {
                        val dao = AppDatabase.getInstance(contexto).compraDao()
                        compra.comprado = true
                        dao.update(compra)
                        withContext(Dispatchers.Main) {
                            onSave()
                        }
                    }
                }
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        // Muestra el nombre del producto
        compra.nombre?.let {
            Text(
                text = it,
                fontSize = 20.sp,
                modifier = Modifier.weight(2f)
            )
        }
        // Botón para eliminar el producto
        Icon(
            Icons.Filled.Delete,
            contentDescription = "Eliminar Producto",
            tint = Color.Red,
            modifier = Modifier.clickable {
                alcanceCorrutina.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance(contexto).compraDao()
                    dao.delete(compra)
                    withContext(Dispatchers.Main) {
                        onSave()
                    }
                }
            }
        )
    }
}

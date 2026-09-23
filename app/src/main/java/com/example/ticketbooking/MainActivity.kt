package com.example.ticketbooking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            // =========================================================
            // STATE HOISTING: semua state dikelola di sini (Parent)
            // =========================================================
            val hargaTiket = 25000
            var jumlahTiket by remember { mutableIntStateOf(1) }
            var namaPembeli by remember { mutableStateOf("") }
            var status by remember { mutableStateOf("Silakan pesan tiket") }
            var isProcessing by remember { mutableStateOf(false) }
            var clickTrigger by remember { mutableIntStateOf(0) } // key untuk LaunchedEffect

            // =========================================================
            // LAUNCHED EFFECT: dijalankan setiap "clickTrigger" berubah
            // (setiap kali tombol "Pesan Tiket" diklik)
            // =========================================================
            LaunchedEffect(clickTrigger) {
                if (clickTrigger == 0) return@LaunchedEffect // belum pernah diklik

                if (namaPembeli.isBlank()) {
                    // Kondisi 1: Nama masih kosong
                    status = "Nama Masih Kosong"
                    isProcessing = false
                } else {
                    // Kondisi 2: Memproses pesanan selama 5 detik
                    isProcessing = true
                    status = "Memproses pesanan....."
                    delay(5000)
                    // Kondisi 3: Setelah 5 detik, pesanan berhasil
                    status = "Tiket telah dipesan"
                    isProcessing = false
                }
            }

            TicketOrderScreen(
                hargaTiket = hargaTiket,
                jumlahTiket = jumlahTiket,
                namaPembeli = namaPembeli,
                status = status,
                isProcessing = isProcessing,
                onNamaChange = { namaPembeli = it },
                onJumlahTambah = { jumlahTiket++ },
                onJumlahKurang = { if (jumlahTiket > 1) jumlahTiket-- },
                onPesanClick = { clickTrigger++ }
            )
        }
    }
}

// =========================================================
// CHILD COMPOSABLE: murni tampilan, TIDAK menyimpan state sendiri.
// Semua data & event diterima lewat parameter dari Parent (hoisting)
// =========================================================
@Composable
fun TicketOrderScreen(
    hargaTiket: Int,
    jumlahTiket: Int,
    namaPembeli: String,
    status: String,
    isProcessing: Boolean,
    onNamaChange: (String) -> Unit,
    onJumlahTambah: () -> Unit,
    onJumlahKurang: () -> Unit,
    onPesanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatRupiah = NumberFormat.getNumberInstance(Locale("in", "ID"))
    val totalBayar = hargaTiket * jumlahTiket

    Column(modifier = modifier.fillMaxSize()) {

        // ---------------- HEADER ----------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E56C7))
                .padding(20.dp)
        ) {
            Text(
                text = "Pemesanan Tiket",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ---------------- INPUT NAMA ----------------
            Column {
                Text(text = "Nama", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = namaPembeli,
                    onValueChange = onNamaChange,
                    placeholder = { Text("Masukkan nama Anda") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // ---------------- JUMLAH TIKET ----------------
            Column {
                Text(text = "Jumlah Tiket", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    IconButton(
                        onClick = onJumlahKurang,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFE3E7EF), CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Kurangi")
                    }

                    Text(
                        text = "$jumlahTiket",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onJumlahTambah,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFE3E7EF), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah")
                    }
                }
            }

            // ---------------- INFO HARGA & TOTAL ----------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Harga per tiket: Rp${formatRupiah.format(hargaTiket)}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total: Rp${formatRupiah.format(totalBayar)}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ---------------- BUTTON PESAN TIKET ----------------
            Button(
                onClick = onPesanClick,
                enabled = !isProcessing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E56C7))
            ) {
                Text(text = "Pesan Tiket", color = Color.White, fontWeight = FontWeight.Bold)
            }

            // ---------------- STATUS ----------------
            val statusBg = when {
                status == "Tiket telah dipesan" -> Color(0xFFE3F6E8)
                status == "Nama Masih Kosong" -> Color(0xFFFCE8E8)
                isProcessing -> Color(0xFFEAF1FC)
                else -> Color(0xFFF0F0F0)
            }
            val statusColor = when {
                status == "Tiket telah dipesan" -> Color(0xFF2E7D32)
                status == "Nama Masih Kosong" -> Color(0xFFC62828)
                else -> Color(0xFF1E56C7)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusBg, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    status == "Tiket telah dipesan" -> Icon(
                        Icons.Default.CheckCircle, contentDescription = null, tint = statusColor
                    )
                    status == "Nama Masih Kosong" -> Icon(
                        Icons.Default.Warning, contentDescription = null, tint = statusColor
                    )
                    isProcessing -> CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = statusColor
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Status: $status",
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
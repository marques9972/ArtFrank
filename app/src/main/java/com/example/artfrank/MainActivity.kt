package com.example.artfrank

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.artfrank.adapter.MovimentacaoAdapter
import com.example.artfrank.model.Movimentacao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputDia: EditText
    private lateinit var inputMes: EditText
    private lateinit var inputAno: EditText
    private lateinit var textMovimentacoes: TextView
    private lateinit var textSemMovimentacoes: TextView
    private lateinit var btnBuscar: Button
    private lateinit var btnAnterior: ImageButton
    private lateinit var btnProximo: ImageButton
    private val dados = mutableListOf<Movimentacao>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializações existentes
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MovimentacaoAdapter(dados)
        textSemMovimentacoes = findViewById(R.id.textSemMovimentacoes)

        inputDia = findViewById(R.id.inputDia)
        inputMes = findViewById(R.id.inputMes)
        inputAno = findViewById(R.id.inputAno)
        textMovimentacoes = findViewById(R.id.textMovimentacoes)
        btnBuscar = findViewById(R.id.btnBuscar)

        // Novas inicializações
        btnAnterior = findViewById(R.id.btnAnterior)
        btnProximo = findViewById(R.id.btnProximo)

        val hoje = Calendar.getInstance()
        inputDia.setText(hoje.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0'))
        inputMes.setText((hoje.get(Calendar.MONTH) + 1).toString().padStart(2, '0')) // Janeiro é 0, então adiciona 1
        inputAno.setText(hoje.get(Calendar.YEAR).toString())

        // Listeners
        btnBuscar.setOnClickListener {
            atualizarDataEBuscar()
        }

        btnAnterior.setOnClickListener {
            ajustarData(-1)
        }

        btnProximo.setOnClickListener {
            ajustarData(1)
        }
    }

    private fun atualizarDataEBuscar() {
        val dia = inputDia.text.toString().padStart(2, '0').toIntOrNull()
        val mes = inputMes.text.toString().padStart(2, '0').toIntOrNull()
        val ano = inputAno.text.toString().toIntOrNull()

        if (dia != null && mes != null && ano != null) {
            buscarDados(ano, mes, dia)
            textMovimentacoes.text = getString(R.string.movimentacoes_do_dia, dia, mes, ano)
        } else {
            Toast.makeText(this, "Por favor, insira uma data válida.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ajustarData(dias: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, inputAno.text.toString().toInt())
            set(Calendar.MONTH, inputMes.text.toString().toInt() - 1)
            set(Calendar.DAY_OF_MONTH, inputDia.text.toString().toInt())
            add(Calendar.DAY_OF_MONTH, dias)
        }

        inputDia.setText(cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0'))
        inputMes.setText((cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')) // Janeiro é 0
        inputAno.setText(cal.get(Calendar.YEAR).toString())

        atualizarDataEBuscar()
    }


    private fun buscarDados(ano: Int, mes: Int, dia: Int) {
        dados.clear()

        val db = Firebase.firestore

        db.collection("informacoes")
            .whereEqualTo("ano", ano)
            .whereEqualTo("mes", mes)
            .whereEqualTo("dia", dia)
            .get()
            .addOnSuccessListener { documentos ->
                if (documentos.isEmpty) {
                    textSemMovimentacoes.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    textSemMovimentacoes.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    for (documento in documentos) {
                        try {
                            val movimentacao = Movimentacao(
                                ano = documento.getLong("ano")?.toInt() ?: 0,
                                createdAt = documento.getLong("createdAt"),
                                descricao = documento.getString("descricao") ?: "",
                                despesa = documento.getString("despesa"),
                                dia = documento.getLong("dia")?.toInt() ?: 0,
                                documentos = documento.get("documentos") as List<String>? ?: emptyList(),
                                mes = documento.getLong("mes")?.toInt() ?: 0,
                                recebimento = documento.getString("recebimento")
                            )
                            dados.add(movimentacao)
                        } catch (e: Exception) {
                            Toast.makeText(this, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar dados: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

}

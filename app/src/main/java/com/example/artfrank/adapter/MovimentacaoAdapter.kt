package com.example.artfrank.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.artfrank.R
import com.example.artfrank.model.Movimentacao
import java.text.NumberFormat
import java.util.*

class MovimentacaoAdapter(private val dados: List<Movimentacao>) :
    RecyclerView.Adapter<MovimentacaoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val descricaoTextView: TextView = view.findViewById(R.id.descricaoTextView)
        val recebimentoTextView: TextView = view.findViewById(R.id.recebimentoTextView)
        val despesaTextView: TextView = view.findViewById(R.id.despesaTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movimentacao, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dados[position]
        val localeBR = Locale("pt", "BR")
        val numberFormat = NumberFormat.getCurrencyInstance(localeBR)

        holder.descricaoTextView.text = item.descricao

        val recebimento = item.recebimento?.toDoubleOrNull()
        holder.recebimentoTextView.visibility = if (recebimento != null && recebimento > 0) View.VISIBLE else View.GONE
        holder.recebimentoTextView.text = recebimento?.let { numberFormat.format(it) } ?: "R$ 0,00"

        val despesa = item.despesa?.toDoubleOrNull()
        holder.despesaTextView.visibility = if (despesa != null && despesa > 0) View.VISIBLE else View.GONE
        holder.despesaTextView.text = despesa?.let { numberFormat.format(it) } ?: "R$ 0,00"
    }




    override fun getItemCount() = dados.size
}

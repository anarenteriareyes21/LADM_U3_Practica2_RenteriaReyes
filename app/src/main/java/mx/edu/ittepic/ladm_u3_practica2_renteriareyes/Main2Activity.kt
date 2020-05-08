package mx.edu.ittepic.ladm_u3_practica2_renteriareyes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {
    /*-------------- OBTENER BD REMOTA-------------*/
    var baseRemota = FirebaseFirestore.getInstance()
    /*---------------- VARIABLES MOSTRAR DATOS------*/
    var dataLista = ArrayList<String>()
    var listaID = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        mostrarPedidos()
    }

    /*--------------------------- MOSTRAR TODOS LOS PEDIDOS------------------------*/
    fun mostrarPedidos(){
        baseRemota.collection("restaurante") //nombre de la coleccion
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    //si es diferente de NULL entonces HAY UN ERROR
                    Toast.makeText(this,"ERROR NO SE PUEDE ACCEDER A CONSULTA", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                dataLista.clear()
                listaID.clear()
                for (document in querySnapshot!!){
                    //leer la informacion de los documentos en la bd remota
                    var cadena = "Nombre: " + document.getString("nombre") + "\n" +
                            "Celular: " + document.getString("celular") + "\n" +
                            "Domicilio: " + document.getString("domicilio") + "\n" +
                            "Datos del pedido: \n" +
                            "Cantidad: " + document.get("pedido.cantidad") + "\n" +
                            "Entregado: " + document.get("pedido.entregado") + "\n" +
                            "Precio: " + document.get("pedido.precio") + "\n" +
                            "Producto: " + document.get("pedido.producto") + "\n"
                    dataLista.add(cadena)
                    listaID.add(document.id) //obtener id del documento
                }
                if (dataLista.size == 0){
                    //la lista esta vacia
                    dataLista.add("NO HAY DATA")
                }
                var adaptador = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,dataLista)
                lista.adapter = adaptador
            }
        lista.setOnItemClickListener { parent, view, position, id ->
            if (listaID.size == 0) {
                return@setOnItemClickListener
            }
            AlertaElimiarActualizar(position)
        }
    }

    /*----------------- MENSAJE PARA ELIMINAR/ACTUALIZAR --------------- */
    private fun AlertaElimiarActualizar(position: Int) {
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage("¿Qué deseas hacer con \n ${dataLista[position]}")
            .setPositiveButton("Eliminar"){d,w->
                // ELIMINAR DATA EN BD REMOTA
                eliminar(listaID[position])
            }
            .setNegativeButton("Actualizar"){d,w->
                llamarVentanaActualizar(listaID[position])
            }
            .setNeutralButton("Cancelar"){d,which->}
            .show()
    }

    /*------------------------- LLAMAR ACTIVITY PARA ACTUALIZAR--------------------*/
    private fun llamarVentanaActualizar(idActualizar: String) {
        baseRemota.collection("restaurante")
            .document(idActualizar)
            .get() //Recuperar la data
            .addOnSuccessListener {
                var v = Intent(this,Main3Activity :: class.java)
                v.putExtra("id",idActualizar)
                v.putExtra("nombre",it.getString("nombre"))
                v.putExtra("celular", it.getString("celular"))
                v.putExtra("domicilio", it.getString("domicilio"))
                v.putExtra("cantidad", it.get("pedido.cantidad").toString())
                v.putExtra("entregado", it.get("pedido.entregado").toString())
                v.putExtra("precio", it.get("pedido.precio").toString())
                v.putExtra("producto", it.get("pedido.producto").toString())
                startActivity(v)
            }
            .addOnFailureListener {
                Toast.makeText(this,"ERROR NO HAY CONEXION DE RED",Toast.LENGTH_LONG).show()
            }
    }

    /*-------------------- METODO PARA ELIMINAR---------------------------------*/
    private fun eliminar(idEliminar: String) {
        baseRemota.collection("restaurante")
            .document(idEliminar) //posicionarse en la bd remota en el id del documento a eliminar
            .delete()
            .addOnSuccessListener {
                //SE ELIMINÓ
                Toast.makeText(this,"SE ELIMINÓ CON ÉXITO",Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener {
                //NO SE ELIMINÓ
                Toast.makeText(this,"NO SE ELIMINÓ",Toast.LENGTH_LONG).show()

            }
    }

}

package com.lendasoft.clubercompanion.Ordenes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lendasoft.clubercompanion.R;

import java.util.List;


/**
 * Created by victorrosas on 2/2/16.
 */
public class OrdenArrayAdapter extends ArrayAdapter<OBJ_ORDEN> {
    public OrdenArrayAdapter(Context context, List<OBJ_ORDEN> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con image_list_view.xml
            listItemView = inflater.inflate(
                    R.layout.image_list_item,
                    parent,
                    false);

        }

        TextView titulo = (TextView) listItemView.findViewById(R.id.text1);
        TextView subtitulo = (TextView) listItemView.findViewById(R.id.text2);
        ImageView categoria = (ImageView) listItemView.findViewById(R.id.category);


        //Obteniendo instancia de la Tarea en la posición actual
        OBJ_ORDEN item = getItem(position);

        titulo.setText("Orden: " + item.getOrderid() + "  " + "Total: " + item.getTotalPayment());
        subtitulo.setText("Mesa: " + item.getTableNumber());
        categoria.setImageResource(item.getPriority());

        //Devolver al ListView la fila creada
        return listItemView;
    }
}

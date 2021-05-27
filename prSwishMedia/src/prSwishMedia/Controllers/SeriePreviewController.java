package prSwishMedia.Controllers;

import prSwishMedia.ContenidoMultimedia;
import prSwishMedia.Serie;
import prSwishMedia.Views.SeriePreView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SeriePreviewController implements ActionListener{

    private SeriePreView pvSerie;
    private Serie contenido;

    public SeriePreviewController(Serie s, SeriePreView sv, JComboBox comboBox){
        pvSerie=sv;
        contenido=s;
        pvSerie.setGenero(contenido.getGenero());
        pvSerie.setNombre(contenido.getNombre());
        pvSerie.setSinopsis(contenido.getSinopsis());
        pvSerie.setImagen(contenido.getId());
        pvSerie.setComboBox(comboBox);
        pvSerie.setValoracion(contenido.getValoracion());
        pvSerie.setNumTemporadas(contenido.getNumTemporadas());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String act = e.getActionCommand();
        switch (act){
            case "VALORAR":
                double valoracionPrevia=contenido.getValoracion();
                int vecesValorada=contenido.getVeces_anyadidas();
                double valoracion= (double) pvSerie.getValoracion();
                double valoracionNueva = ((valoracionPrevia*vecesValorada)+valoracion)/(vecesValorada+1);
                contenido.setVeces_anyadidas(vecesValorada+1);
                contenido.setRating((int) valoracionNueva);
                pvSerie.setMsgInfo("Valorada con éxito");
                break;
        }
    }
    public void actualizarComboBox(JComboBox l){
        pvSerie.setComboBox(l);
    }
}

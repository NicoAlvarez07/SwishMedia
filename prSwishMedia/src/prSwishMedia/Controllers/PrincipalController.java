package prSwishMedia.Controllers;

import prSwishMedia.Lista;
import prSwishMedia.Main;
import prSwishMedia.Usuario;
import prSwishMedia.Views.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PrincipalController implements ActionListener {

    LoginView lview;
    Statement conexion;
    PrincipalView ppView;
    Usuario user;
    public PrincipalController(LoginView lv, PrincipalView ppv, Statement st, Usuario u){
        conexion=st;
        lview=lv;
        ppView=ppv;
        user=u;
        setLista();
        añadirContenidoPelicula(-2);
        añadirContenidoSerie(-2);
    }


    public void actionPerformed(ActionEvent e) {
        String act=e.getActionCommand();

        if(act.equals("PROFILE")){
            ProfileView pview = new ProfileView(conexion);
            ProfileController pc = new ProfileController(pview,ppView,lview,conexion,user);
            pview.controlador(pc);

            Main.frame.setContentPane(pview.getPanel());
            Main.frame.setVisible(true);
        }
    }

    public void añadirContenidoPelicula(int idList){
        ppView.removeAllListas();
        if(idList==-2){

            try {
                ResultSet count= conexion.executeQuery("SELECT COUNT(*) FROM ContenidoMultimedia join Pelicula on ContenidoMultimedia.idContenidoMultimedia=Pelicula.idContenidoMultimedia;");
                count.next();
                int cont=count.getInt(1);
                ResultSet peli= conexion.executeQuery("SELECT * FROM ContenidoMultimedia join Pelicula on ContenidoMultimedia.idContenidoMultimedia=Pelicula.idContenidoMultimedia;");
                ppView.setLayoutListas(cont);

                // Creo lista para almacenar los idContenidoMultimedia y referencias de pelipv
                ArrayList<Integer> listaids = new ArrayList<>();
                ArrayList<PeliculaPreView> listapelipv = new ArrayList<>();
                while(peli.next()) {
                    // Necesitamos guardar las variables antes de hacer una nueva consulta,
                    // si no, se borra la informacion de la anterior consulta
                    listaids.add(peli.getInt("idContenidoMultimedia"));

                    PeliculaPreView pelipv = new PeliculaPreView(peli.getString("nombre"), peli.getInt("imagen"), peli.getString("sinopsis"), peli.getString("genero"), 0, ppView.getComboBox1());

                    listapelipv.add(pelipv);
                    MiMouseListener listener = new MiMouseListener(pelipv,peli.getInt(1));
                    pelipv.getPanel().addMouseListener(listener);
                    ppView.addListaPelis(pelipv.getPanel());
                }
                for (int i = 0; i < listaids.size(); i++) {
                    //Por cada peli generamos su valoracion media
                    ResultSet valmed = conexion.executeQuery("SELECT IFNULL(AVG(valoracion),0) FROM Valora WHERE idContenido=" + listaids.get(i) + ";");
                    valmed.next(); //apuntamos
                    listapelipv.get(i).setValoracion(valmed.getInt(1));
                }

                ppView.setViewportViewScroll(ppView.getListaPelis());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }


    public void añadirContenidoSerie(int idList){
        ppView.removeAllListasSerie();
        if(idList==-2){

            try {
                ResultSet count= conexion.executeQuery("SELECT COUNT(*) FROM ContenidoMultimedia join Serie on ContenidoMultimedia.idContenidoMultimedia=Serie.idContenidoMultimedia;");
                count.next();
                int cont=count.getInt(1);
                ResultSet peli= conexion.executeQuery("SELECT * FROM ContenidoMultimedia join Serie on ContenidoMultimedia.idContenidoMultimedia=Serie.idContenidoMultimedia;");
                ppView.setLayoutListasSerie(cont);
                while(peli.next()) {
                    SeriePreView seriepv = new SeriePreView(peli.getString("nombre"), peli.getInt("imagen"), peli.getString("sinopsis"), 0, ppView.getComboBox1(), peli.getInt("numTemporadas"));
                    //MiMouseListener listener = new MiMouseListener();
                    //seriepv.getPanel().addMouseListener(listener);
                    ppView.addListaSerie(seriepv.getPanel());
                }
                ppView.setViewportViewScrollSerie(ppView.getListaSeries());


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }

    }

    public void actualizarComboBox() {
        ppView.removeAllComboBox();
        if(user.getListasPersonales()!=null){
            for(Lista l: user.getListasPersonales()){
                System.out.println(l.toString());
                ppView.addItemComboBox1(l);
            }
        }else {
            System.out.println("LISTA VACIA");
        }
    }

    public void setLista(){
        ArrayList<Lista> listaActualizada = new ArrayList<>();
        Lista actual;

        ppView.removeAllComboBox();
        try {
            ResultSet rs = conexion.executeQuery("SELECT * FROM Lista where Nombreusuario = '"+user.getNombre()+"';");
            while(rs.next()){
                actual=new Lista(rs.getInt(1),rs.getString(2),rs.getDate(3));
                listaActualizada.add(actual);
                ppView.addItemComboBox1(actual);
            }
            user.setListasPersonales(listaActualizada);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public class MiMouseListener implements MouseListener {
        PeliculaPreView pview;
        int id;
        public MiMouseListener(PeliculaPreView pelipv, int anInt) {
            pview=pelipv;
            id=anInt;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                ResultSet resst = conexion.executeQuery("SELECT * FROM ContenidoMultimedia, Pelicula where ContenidoMultimedia.idContenidoMultimedia="+id+";");
                resst.next();
                PeliculaView peliview = new PeliculaView(resst.getString("nombre"),0,resst.getString("fecha_estreno"),resst.getInt("duracion"), resst.getString("genero"), resst.getString("sinopsis"),resst.getString("reparto"));
                Main.frame.setContentPane(peliview.getPanel());
                Main.frame.setVisible(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            //System.out.println("hola2");
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            //System.out.println("hola3");
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //System.out.println("hola4");
        }

        @Override
        public void mouseExited(MouseEvent e) {
            //System.out.println("hola5");
        }
    }
}

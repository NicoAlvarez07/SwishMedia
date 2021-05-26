package prSwishMedia.Controllers;

import prSwishMedia.Main;
import prSwishMedia.Pelicula;
import prSwishMedia.Views.PeliculaView;
import prSwishMedia.Views.PrincipalView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.*;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import prSwishMedia.Usuario;
import prSwishMedia.Views.ComentarioView;
import prSwishMedia.Views.ComentariosDeOtros;
import prSwishMedia.Views.PeliculaView;

import javax.swing.*;
import java.awt.event.KeyEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class PeliculaController implements ActionListener, KeyListener {

    private PeliculaView peliview;
    private boolean porderEntregar;
    private Usuario user;
    private Statement conexion;
    private int IDContenido;
    private JPanel listaComentarios = new JPanel();
    PrincipalView principalView;
    Pelicula pelicula;

    public PeliculaController(PeliculaView peliview, Usuario u, Statement st, Pelicula p, PrincipalView ppv, int id) throws SQLException {
        this.peliview=peliview;
        user=u;
        conexion=st;
        pelicula=p;
        principalView=ppv;
        IDContenido=id;
        setInfo();
        actualizarComentarios();
    }

    private void setInfo() {
        peliview.setNombrePelicula(pelicula.getNombre());
        peliview.setValoracionPelicula(Integer.toString(pelicula.getRating()));
        peliview.setFechaPelicula(pelicula.getFecha().toString());
        peliview.setPeliculaDuracion(pelicula.getDuracion());
        peliview.setPeliculaGénero(pelicula.getGenero());
        peliview.setSinopsisPelicula(pelicula.getSinopsis());
        peliview.setRepartoPelicula(pelicula.getReparto());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String act = e.getActionCommand();
        switch (act){
            case ("VOLVER"):
                Main.frame.setContentPane(principalView.getPanel());
                Main.frame.setVisible(true);
                break;
            case ("TRAILER"):
                System.out.println("trailer");
                break;
            case ("ENVIAR"):
                try {
                    introducirComentario();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
        }
    }

    private int generateID () throws SQLException {
        ResultSet res = conexion.executeQuery("SELECT MAX(id) FROM Comunicación;");
        int id=0;
        try{
            res.next();
            id=res.getInt("MAX(id)")+1;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void introducirComentario() throws SQLException {
        int ID = generateID();
        try {
            conexion.executeUpdate("Insert into Comunicación (texto, fechaEnvio, Usuario, ID) values('"+peliview.getTextFieldComentarios().getText()+"',sysdate(),'"+user.getNombre()+"',"+ID+");");
            conexion.executeUpdate("Insert Into Comentario (numDislikes,numLikes,ID,IDContenido) values(0,0,"+ID+","+IDContenido+");");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        actualizarComentarios();
    }

    public void actualizarComentarios(){
        listaComentarios.removeAll();
        try {
            ResultSet rs3 = conexion.executeQuery("SELECT COUNT(*) FROM Comentario, Comunicación where IDContenido="+IDContenido+" and Comentario.ID=Comunicación.ID");
            rs3.next();
            int cont = rs3.getInt(1);
            listaComentarios.setLayout(new GridLayout(cont,0,0,0));
            ResultSet rs2 = conexion.executeQuery("SELECT * FROM Comunicación, Comentario where IDContenido="+IDContenido+" and Comentario.ID=Comunicación.ID");
            while(rs2.next()){
                if(user.getNombre().equals(rs2.getString("Usuario"))){
                    listaComentarios.add(new ComentarioView(rs2.getString("texto"),rs2.getInt("numDislikes"), rs2.getInt("numLikes"),rs2.getString("Usuario"),rs2.getString("fechaEnvio")).get());
                } else {
                    listaComentarios.add(new ComentariosDeOtros(rs2.getString("texto"),rs2.getInt("numDislikes"), 0,rs2.getString("Usuario"),rs2.getString("fechaEnvio")).get());
                }
            }
            peliview.setComentariosPanel(listaComentarios);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(peliview.getTextFieldComentarios().getText().length()==100){
            e.consume();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()!=10&&e.getKeyCode()!=32){
            porderEntregar=true;
        }
        if(e.getKeyCode()==222){
            e.consume();
        } else if(e.getKeyCode()==10&&porderEntregar){
            System.out.println("hola");
            e.consume();
            try {
                introducirComentario();
            } catch (SQLException throwables) {
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

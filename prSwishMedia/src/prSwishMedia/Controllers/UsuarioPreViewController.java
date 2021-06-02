package prSwishMedia.Controllers;

import prSwishMedia.Main;
import prSwishMedia.Usuario;
import prSwishMedia.Views.UsuarioPreView;

import javax.swing.plaf.nimbus.State;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioPreViewController implements ActionListener {

    private UsuarioPreView userPv;
    private Usuario user;
    private Statement conexion;
    private Usuario tu;

    public UsuarioPreViewController(UsuarioPreView userpv, Usuario usuario, Statement st, Usuario u2){
        userPv=userpv;
        user=usuario;
        conexion=st;
        tu=u2;
        userPv.setNombre(user.getNombre());
        userPv.setDescripcion(user.getDescripcion());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String act = e.getActionCommand();
        switch (act) {
            case "AÑADIRAMIGO":
                System.out.println("hola");
                try {
                    ResultSet rs = conexion.executeQuery("SELECT * FROM Usuario where nombre='" + user.getNombre() + "';");
                    rs.next();
                    if (rs.getBoolean("privacidad")) {
                        System.out.println("hola2");
                        conexion.executeUpdate("INSERT INTO Amigo (id, usuario1, usuario2, solicitud) values(" + generateID() + ",'" + user.getNombre() + "','" + tu.getNombre() + "', true)");
                    } else {
                        System.out.println("hola3");
                        conexion.executeUpdate("INSERT INTO Amigo (id, usuario1, usuario2, isAmigo) values(" + generateID() + ",'" + user.getNombre() + "','" + tu.getNombre() + "', true)");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
        }
    }
    private int generateID(){
        ResultSet res = null;
        try {
            res = conexion.executeQuery("SELECT MAX(id) FROM Amigo;");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        int id=0;
        try{
            res.next();
            id=res.getInt("MAX(id)")+1;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}


import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.ListDataListener;

import java.util.Date;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        try {
            Connection banco = conectarBanco();
            // objeto que executa os comandos sql.
            Statement executor = banco.createStatement();

            // criando a string de comando do sql
            // https://www.tutorialspoint.com/sqlite/index.htm
            String querySql = "CREATE TABLE USUARIOS " +
                    "(ID     INTEGER PRIMARY KEY," +
                    "NOME    TEXT                    NOT NULL," +
                    "EMAIL   TEXT," +
                    "HORA_CADASTRO   TIMESPAN        NOT NULL)";
            // Executar o comando e fechar o executor pra liberar o banco de dados
            executor.executeUpdate(querySql);
            executor.close();
            //Fechar conexão com o banco nem sempre é nescessário, vai depender da arquitetura do projeto
            banco.close();
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }

        //Buscar usuarios no banco de dados
        ArrayList<String> usuarios = buscarUsuarios();

        JFrame janela = new JFrame();
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JList<String> lista = new JList<>();
        lista.setListData(usuarios.toArray(new String[0]));
        lista.setSize(200, 400);
        janela.add(lista);
        System.out.println(usuarios.size());

        JLabel labelNome = new JLabel("Nome: ");
        labelNome.setSize(80, 20);
        JTextField fieldNome = new JTextField();
        //fieldNome.setSize(200, 30);
        janela.add(labelNome);
        janela.add(fieldNome);

        JLabel labelEmail = new JLabel("Email: ");
        labelEmail.setSize(80,20);
        JTextField fieldEmail = new JTextField();
        fieldEmail.setSize(200,30);
        janela.add(labelEmail);
        janela.add(fieldEmail);

        JButton inserirUsuario = new JButton("Salvar");
        inserirUsuario.setSize(80, 30);
        inserirUsuario.addActionListener(evento -> {
            String nome = fieldNome.getText();
            String email = fieldEmail.getText();

            try {
                Connection database = conectarBanco();

                //valores em String devem ter aspas simples '
                //valores booleanos e numericos não possuem aspas
                String inserirComando = "INSERT INTO USUARIOS (NOME,EMAIL,HORA_CADASTRO) " +
                        "VALUES ('"+ nome + "', '" + email + "', " + new Date(System.currentTimeMillis()).getTime() + ");";
                Statement executor = database.createStatement();
                executor.executeUpdate(inserirComando);
                database.close();

                var u = buscarUsuarios();
                lista.setListData(u.toArray(new String[0]));
            }catch(Exception e) {
                System.err.println(e.getLocalizedMessage());
            }
        });
        janela.add(inserirUsuario);
        janela.setLayout(new GridLayout(6, 2));
        janela.setSize(800,600);
        janela.setVisible(true);
    }

    private static ArrayList<String> buscarUsuarios() {
        ArrayList<String> usuarios = new ArrayList<>();
        try {
            Connection banco = conectarBanco();
            Statement executor = banco.createStatement();
            // Buscar por todos os usuarios cadastrados no banco para exibir em uma lista
            ResultSet resultado = executor.executeQuery("SELECT * FROM USUARIOS");
            while (resultado.next()) {
                Usuario usuario = new Usuario();
                //usar metodo get para cada tipo de dado, utilizando o nome exato na criação da tabela
                int id = resultado.getInt("ID");
                usuario.Nome = resultado.getString("NOME");
                usuario.Email = resultado.getString("EMAIL");
                usuario.HoraCadastro = resultado.getDate("HORA_CADASTRO");

                usuarios.add("ID: " + id + " | Nome: " + usuario.Nome + " | Email: " + usuario.Email + " | Cadastrado: " + usuario.HoraCadastro);
            }
            //Fechar o executor e a conexão após leitura de todos os usuarios
            executor.close();
            banco.close();
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        return usuarios;
    }

    static Connection conectarBanco(){
        Connection dbConnection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            // Connectar ao banco de dados, nome do bando pode ser qualquer um.
            dbConnection = DriverManager.getConnection("jdbc:sqlite:nome_do_banco.db");
            System.out.println("Conectado ao banco SQLite!");
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
        return dbConnection;
    }
}
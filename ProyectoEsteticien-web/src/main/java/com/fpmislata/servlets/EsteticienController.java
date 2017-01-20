/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpmislata.servlets;

import com.fpmislata.domain.Cita;
import com.fpmislata.domain.Cliente;
import com.fpmislata.domain.Producto;
import com.fpmislata.domain.Tratamiento;
import com.fpmislata.domain.Venta;
import com.fpmislata.service.CitaServiceLocal;
import com.fpmislata.service.ClienteServiceLocal;
import com.fpmislata.service.ProductoServiceLocal;
import com.fpmislata.service.TratamientoServiceLocal;
import com.fpmislata.service.VentaServiceLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author DanielPerez
 */
@WebServlet(name = "EsteticienController",
        loadOnStartup = 1,
        urlPatterns = {"/AltaCliente",
            "/AltaTratamiento",
            "/ConcertarCita",
            "/EliminarCliente",
            "/EliminarTratamiento",
            "/ListarCitas",
            "/ListarClientes",
            "/ListarTratamientos",
            "/ModificarCliente",
            "/ModificarTratamiento",
            "/VentaProductos"})

public class EsteticienController extends HttpServlet {

    @EJB
    private VentaServiceLocal ventaService;

    @EJB
    private TratamientoServiceLocal tratamientoService;

    @EJB
    private ProductoServiceLocal productoService;

    @EJB
    private ClienteServiceLocal clienteService;

    @EJB
    private CitaServiceLocal citaService;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userPath = request.getServletPath();

        // Si la operacion es agregar cliente
        if (userPath.equals("/AltaCliente")) {
            altaCliente(request, response);

            // Si la operacion es agregr tratamiento
        } else if (userPath.equals("/AltaTratamiento")) {
            altaTratamiento(request, response);

            // Si la operacion es concertar cita
        } else if (userPath.equals("/ConcertarCita")) {
            concertarCita(request, response);

            // Si la operacion es eliminar cliente
        } else if (userPath.equals("/EliminarCliente")) {
            eliminarCliente(request, response);

            // Si la operacion es eliminar tratamiento
        } else if (userPath.equals("/EliminarTratamiento")) {
            eliminarTratamiento(request, response);

            // Si la operacion es listar citas
        } else if (userPath.equals("/ListarCitas")) {
            listarCitas(request, response);

            // Si la operacion es listar clientes
        } else if (userPath.equals("/ListarClientes")) {
            listarClientes(request, response);

            // Si la operacion es lostar tratamientos
        } else if (userPath.equals("/ListarTratamientos")) {
            listarTratamientos(request, response);

            // Si la operacion es modificar cliente
        } else if (userPath.equals("/ModificarCliente")) {
            modificarCliente(request, response);

            // Si la operacion es modificar tratamiento
        } else if (userPath.equals("/ModificarTratamiento")) {
            modificarTratamiento(request, response);

            // Si la operacion es venta productos
        } else if (userPath.equals("/VentaProductos")) {
            ventaProductos(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void altaCliente(HttpServletRequest request, HttpServletResponse response) {
        try {
            String nombre = request.getParameter("nombre");
            String apellidos = request.getParameter("apellidos");
            String dni = request.getParameter("dni");
            String telefono = request.getParameter("telefono");
            String email = request.getParameter("email");

            Cliente clienteAnyadir = new Cliente(0, nombre, apellidos, dni, telefono, email);
            clienteService.agregar(clienteAnyadir);

            ArrayList<Cliente> clientes = clienteService.listar();
            request.getSession().setAttribute("listaClientes", clientes);
            RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/listarCliente.jsp");
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void altaTratamiento(HttpServletRequest request, HttpServletResponse response) {
        try {
            //1. Recuperamos los parametros
            String nombre = request.getParameter("nombre");
            double precio = Double.parseDouble(request.getParameter("precio"));
            double duracion = Double.parseDouble(request.getParameter("duracion"));
            int sala = Integer.parseInt(request.getParameter("sala"));

            //2. Creamos el objeto Tratamiento
            Tratamiento tratamiento = new Tratamiento();
            tratamiento.setNombreTrat(nombre);
            tratamiento.setPrecioTrat(precio);
            tratamiento.setDuracionTrat(duracion);
            tratamiento.setSala(sala);

            try {
                tratamientoService.addTratamiento(tratamiento);
            } catch (Exception e) {
                //Informamos cualquier error
                e.printStackTrace();
            }

            // Volvemos a cargar la lista de personas
            ArrayList<Tratamiento> lista = tratamientoService.listaTratamientos();
            request.setAttribute("tratamientos", lista);

            request.getRequestDispatcher("/listarTratamientos.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void concertarCita(HttpServletRequest request, HttpServletResponse response) {
        try {
            String accion = request.getParameter("accion");

            if (accion.equals("primero")) {

                try {
                    // Ejecutamos el metodo y obtenemos la lista de clientes y tratamientos
                    ArrayList listaCli = clienteService.listar();
                    ArrayList listaTrat = tratamientoService.listaTratamientos();
                    // Asignamos al request el atributo lista 
                    request.getSession().setAttribute("listaCli", listaCli);
                    request.getSession().setAttribute("listaTrat", listaTrat);
                    // Pasamos al RequestDispatcher la pagina a cargar
                    RequestDispatcher rd = request.getRequestDispatcher("/seleccionCita.jsp");
                    // Cargamos la pagina
                    rd.forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (accion.equals("segundo")) {

                //Recogemos los datos para crear la cita
                int idCli = Integer.parseInt(request.getParameter("clienteChecked"));
                int idTrat = Integer.parseInt(request.getParameter("tratamientoChecked"));
                String dia = request.getParameter("dia");
                String hora = request.getParameter("hora");

                //2. Creamos el objeto Cita
                Cita cita = new Cita();
                cita.setIdCliente(idCli);
                cita.setIdTratamiento(idTrat);
                cita.setFecha(dia);
                cita.setHora(hora);

                try {
                    citaService.addCita(cita);
                } catch (Exception e) {
                    //Informamos cualquier error
                    e.printStackTrace();
                }

                // Pasamos al RequestDispatcher la pagina a cargar
                String citaAsignada = "1";//funciona como un switch
                request.setAttribute("citaAsignada", citaAsignada);
                RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
                // Cargamos la pagina
                rd.forward(request, response);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarCliente(HttpServletRequest request, HttpServletResponse response) {
        try {
            String[] clientesMarcados = request.getParameterValues("clienteChecked");
            if (clientesMarcados != null) {
                for (int i = 0; i < clientesMarcados.length; i++) {
                    int idCliente = Integer.parseInt(clientesMarcados[i]);
                    Cliente clienteABorrar = new Cliente(idCliente, "", "", "", "", "");
                    clienteService.borrar(clienteABorrar);
                }
            }
            ArrayList<Cliente> clientes = clienteService.listar();
            request.getSession().setAttribute("listaClientes", clientes);
            RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/listarCliente.jsp");
            rd.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarTratamiento(HttpServletRequest request, HttpServletResponse response) {
        try {
            //1. Recuperamos los parametros
            String idTratamiento = request.getParameter("id");

            //2. Creamos el objeto Tratamiento
            int id = Integer.parseInt(idTratamiento);
            Tratamiento tratamiento = new Tratamiento();
            tratamiento.setId(id);

            try {
                //3. Eliminamos el tratamiento
                this.tratamientoService.deleteTratamiento(tratamiento);
            } catch (Exception e) {
                //Informamos cualquier error
                e.printStackTrace();
            }

            // Ejecutamos el metodo y obtenemos la lista
            ArrayList lista = tratamientoService.listaTratamientos();
            // Asignamos al request el atributo lista
            request.getSession().setAttribute("tratamiento", lista);
            // Pasamos al RequestDispatcher la pagina a cargar
            RequestDispatcher rd = request.getRequestDispatcher("/listarTratamientos.jsp");
            // Cargamos la pagina
            rd.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listarCitas(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Ejecutamos el metodo y obtenemos la lista
            ArrayList<Cita> listaCita = citaService.listaCitas();
            ArrayList<Cliente> listaCli = new ArrayList<Cliente>();
            ArrayList<Tratamiento> listaTrat = new ArrayList<Tratamiento>();

            for (Cita cita : listaCita) {
                Cliente cli = new Cliente(cita.getIdCliente(), "", "", "", "", "");
                Cliente cliente = clienteService.mostrarUno(cli);
                //Cliente cliente= clienteService.muestraUnoId(cita.getIdCliente());
                Tratamiento tra = new Tratamiento();
                tra.setId(cita.getIdTratamiento());
                //Tratamiento tratamiento= tratamientoService.muestraUnoId(cita.getIdTratamiento());
                listaCli.add(cliente);
                Tratamiento tratamiento = tratamientoService.mostrarUno(tra);
                listaTrat.add(tratamiento);
            }

            // Asignamos al request el atributo lista 
            request.getSession().setAttribute("citas", listaCita);
            request.getSession().setAttribute("clientes", listaCli);
            request.getSession().setAttribute("tratamientos", listaTrat);
            // Pasamos al RequestDispatcher la pagina a cargar
            RequestDispatcher rd = request.getRequestDispatcher("/listarCitas.jsp");
            // Cargamos la pagina
            rd.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listarClientes(HttpServletRequest request, HttpServletResponse response) {
        try {
            ArrayList<Cliente> clientes = clienteService.listar();
            request.getSession().setAttribute("listaClientes", clientes);
            RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/listarCliente.jsp");
            rd.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listarTratamientos(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Ejecutamos el metodo y obtenemos la lista
            ArrayList lista = tratamientoService.listaTratamientos();
            // Asignamos al request el atributo lista 
            request.getSession().setAttribute("tratamientos", lista);
            // Pasamos al RequestDispatcher la pagina a cargar
            RequestDispatcher rd = request.getRequestDispatcher("/listarTratamientos.jsp");
            // Cargamos la pagina
            rd.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modificarCliente(HttpServletRequest request, HttpServletResponse response) {
        try {
            ArrayList<Cliente> listaClientes = (ArrayList<Cliente>) request.getSession().getAttribute("listaClientes");

            String accion = request.getParameter("accion");
            if (accion.equals("cambiarDatos")) {
                String[] clientesMarcados = request.getParameterValues("clienteChecked");
                if (clientesMarcados != null) {
                    ArrayList<Cliente> clientesAModificar = new ArrayList<Cliente>();
                    for (int i = 0; i < clientesMarcados.length; i++) {
                        int idClienteMarcado = Integer.parseInt(clientesMarcados[i]);
                        for (int j = 0; j < listaClientes.size(); j++) {
                            if (listaClientes.get(j).getId() == idClienteMarcado) {
                                clientesAModificar.add(listaClientes.get(j));
                            }
                        }
                    }
                    request.setAttribute("clientesModificar", clientesAModificar);
                    RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/modificarCliente.jsp");
                    rd.forward(request, response);
                } else {
                    RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/listarClientes.jsp");
                    rd.forward(request, response);
                }
            }
            if (accion.equals("confirmarModif")) {
                String[] idModificar = request.getParameterValues("id");
                String[] nombres = request.getParameterValues("nombre");
                String[] apellidos = request.getParameterValues("apellidos");
                String[] dnis = request.getParameterValues("dni");
                String[] telefonos = request.getParameterValues("telefono");
                String[] emails = request.getParameterValues("email");

                for (int i = 0; i < idModificar.length; i++) {
                    int idCliente = Integer.parseInt(idModificar[i]);
                    Cliente cliente = new Cliente(idCliente, nombres[i], apellidos[i], dnis[i], telefonos[i], emails[i]);
                    clienteService.modificar(cliente);

                }

                ArrayList<Cliente> clientes = clienteService.listar();
                request.getSession().setAttribute("listaClientes", clientes);
                RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/listarCliente.jsp");
                rd.forward(request, response);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void modificarTratamiento(HttpServletRequest request, HttpServletResponse response) {
        try {
            String accion = request.getParameter("accion");

            if (accion != null && accion.equals("editar")) {

                //1. Recuperamos el id del tratamiento seleccionado
                String idTratamiento = request.getParameter("id");
                if (idTratamiento != null) {
                    //2. Creamos el objeto tratamiento a recuperar
                    int id = Integer.valueOf(idTratamiento);
                    Tratamiento tratamiento = new Tratamiento();
                    tratamiento.setId(id);
                    try {
                        tratamiento = this.tratamientoService.findTratamientoById(tratamiento);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //3. Compartimos el objeto tratamiento en alcance request
                    request.setAttribute("tratamiento", tratamiento);

                    //4. Redireccionamos a la pagina para editar el objeto Tratamiento
                    request.getRequestDispatcher("/modificarTratamiento.jsp").forward(request, response);
                }
            } else if (accion != null && accion.equals("modificar")) {

                //1. Recuperamos los parametros
                String idTratamiento = request.getParameter("id");
                String nombre = request.getParameter("nombre");
                double precio = Double.parseDouble(request.getParameter("precio"));
                double duracion = Double.parseDouble(request.getParameter("duracion"));
                int sala = Integer.parseInt(request.getParameter("sala"));

                //2. Creamos el objeto Tratamiento
                Tratamiento tratamiento = new Tratamiento();
                int id = Integer.parseInt(idTratamiento);
                tratamiento.setId(id);
                tratamiento.setNombreTrat(nombre);
                tratamiento.setPrecioTrat(precio);
                tratamiento.setDuracionTrat(duracion);
                tratamiento.setSala(sala);

                try {
                    this.tratamientoService.updateTratamiento(tratamiento);
                } catch (Exception e) {
                    //Informamos cualquier error
                    e.printStackTrace();
                }

                // Volvemos a cargar la lista de personas
                ArrayList<Tratamiento> lista = tratamientoService.listaTratamientos();
                request.setAttribute("tratamientos", lista);

                request.getRequestDispatcher("/listarTratamientos.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ventaProductos(HttpServletRequest request, HttpServletResponse response) {
        try {
            int idCliente;
        String accion = request.getParameter("accion");//debe venir del envio desde el index.jsp
        String[] idProductos;
        ArrayList<Producto> productosVendidos = new ArrayList<>();
        
        switch(accion){
            case "comienzo":
                ArrayList<Cliente> listaClientes = (ArrayList<Cliente>) request.getSession().getAttribute("listaClientes");
                if(listaClientes==null){
                    listaClientes=clienteService.listar();
                }
                request.getSession().setAttribute("listaClientes", listaClientes);
                RequestDispatcher rd = this.getServletContext().getRequestDispatcher("/seleccionClientesVentas.jsp");
                rd.forward(request, response);
            ;break;
            case "selectCliente":
                idCliente = Integer.parseInt(request.getParameter("clienteChecked"));
                request.getSession().setAttribute("idCliente", idCliente);
                                
                ArrayList<Producto> listaProductos = productoService.listarProductos();
                
                request.setAttribute("listaProductos",listaProductos);
                rd = this.getServletContext().getRequestDispatcher("/seleccionProductosVentas.jsp");
                rd.forward(request, response);
            ;break;
            case "selectProducto":
                idCliente= (int) request.getSession().getAttribute("idCliente");
                Cliente cl = new Cliente(idCliente,"","","","","");
                Cliente cliente = clienteService.mostrarUno(cl);
                
                Double precioTotalVenta=0.0;
                
                idProductos = (request.getParameterValues("productoChecked"));//este array es de Strings
                request.getSession().setAttribute("idProductos", idProductos);
                if(idProductos!=null){
                    for(int i=0;i<idProductos.length;i++){
                        int idProd = Integer.parseInt(idProductos[i]);
                        Producto prod=new Producto(idProd,"","",0);
                        Producto producto = productoService.mostrarUno(prod);
                        productosVendidos.add(producto);
                        precioTotalVenta+=producto.getPrecioProducto();
                    }
                }
                
                request.setAttribute("cliente", cliente);
                request.setAttribute("prodsVendidos", productosVendidos);
                request.setAttribute("precioTotal", precioTotalVenta);
                rd = this.getServletContext().getRequestDispatcher("/finalizarVentaProductos.jsp");
                rd.forward(request, response);
                
            ;break;
            case "confirmarVenta":
                idCliente= (int) request.getSession().getAttribute("idCliente");
                idProductos= (String[]) request.getSession().getAttribute("idProductos");
                String fechaVenta = request.getParameter("fechaVenta");
                for(int i=0;i<idProductos.length;i++){
                        int idProd = Integer.parseInt(idProductos[i]);
                        Venta venta = new Venta(0,idCliente,idProd,fechaVenta);
                        ventaService.agregarVenta(venta);
                }
                String ventaRealizada="1";//funciona como un switch
                request.setAttribute("ventaRealizada", ventaRealizada);
                rd = this.getServletContext().getRequestDispatcher("/index.jsp");
                rd.forward(request, response);
            ;break;        
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
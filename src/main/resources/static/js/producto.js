/*=========================================
    INVENTARIO - HU03 / HU04 / HU05
=========================================*/

const modalProducto = document.getElementById("modalProducto");
const modalOferta = document.getElementById("modalOferta");
const modalEliminar = document.getElementById("modalEliminar");

/*=========================================
    MODAL PRODUCTO
=========================================*/

function abrirModalNuevoProducto(){

    limpiarFormulario();

    document.getElementById("tituloModalProducto").innerHTML =
        "📦 Registrar Nuevo Producto";

    modalProducto.classList.add("activo");

}

function cerrarModalProducto(){

    modalProducto.classList.remove("activo");

}

/*=========================================
    EDITAR PRODUCTO
=========================================*/

function abrirModalEditarProducto(btn){

    limpiarFormulario();

    document.getElementById("tituloModalProducto").innerHTML =
        "✏ Editar Producto";

    document.getElementById("idProducto").value =
        btn.dataset.id;

    document.getElementById("nombre").value =
        btn.dataset.nombre;

    document.getElementById("codigoBarras").value =
        btn.dataset.codigo || "";

    document.getElementById("idCategoria").value =
        btn.dataset.categoria || "";

    document.getElementById("descripcion").value =
        btn.dataset.descripcion || "";

    document.getElementById("imagenUrl").value =
        btn.dataset.imagen || "";

    document.getElementById("precioCompra").value =
        btn.dataset.preciocompra || "";

    document.getElementById("precioVenta").value =
        btn.dataset.precioventa || "";

    document.getElementById("stockActual").value =
        btn.dataset.stock || "";

    document.getElementById("stockMinimo").value =
        btn.dataset.stockminimo || "";

    document.getElementById("fechaVencimiento").value =
        btn.dataset.vencimiento || "";

    document.getElementById("estado").value =
        btn.dataset.estado || 1;

    modalProducto.classList.add("activo");

}

/*=========================================
    LIMPIAR
=========================================*/

function limpiarFormulario(){

    document.getElementById("formProducto").reset();

    document.getElementById("idProducto").value="";

}

/*=========================================
    MODAL OFERTA
=========================================*/

function abrirModalOferta(btn){

    document.getElementById("ofertaIdProducto").value =
        btn.dataset.id;

    document.getElementById("tituloModalOferta").innerHTML =
        "⚡ Oferta - " + btn.dataset.nombre;

    document.getElementById("ofertaPrecioVenta").value =
        "S/ " + btn.dataset.precio;

    document.getElementById("ofertaDescuento").value =
        btn.dataset.descuento;

    modalOferta.classList.add("activo");

}

function cerrarModalOferta(){

    modalOferta.classList.remove("activo");

}

/*=========================================
    MODAL ELIMINAR
=========================================*/

function abrirModalEliminar(btn){

    document.getElementById("eliminarIdProducto").value =
        btn.dataset.id;

    document.getElementById("eliminarNombreProducto").innerHTML =
        btn.dataset.nombre;

    modalEliminar.classList.add("activo");

}

function cerrarModalEliminar(){

    modalEliminar.classList.remove("activo");

}

/*=========================================
    CERRAR MODAL CON ESC
=========================================*/

document.addEventListener("keydown",function(e){

    if(e.key==="Escape"){

        cerrarModalProducto();

        cerrarModalOferta();

        cerrarModalEliminar();

    }

});

/*=========================================
    CERRAR AL HACER CLICK FUERA
=========================================*/

window.addEventListener("click",function(e){

    if(e.target===modalProducto){

        cerrarModalProducto();

    }

    if(e.target===modalOferta){

        cerrarModalOferta();

    }

    if(e.target===modalEliminar){

        cerrarModalEliminar();

    }

});

/*=========================================
    VALIDACIÓN CLIENTE
=========================================*/

const formulario =
    document.getElementById("formProducto");

if(formulario){

    formulario.addEventListener("submit",function(e){

        const nombre =
            document.getElementById("nombre").value.trim();

        const precioVenta =
            parseFloat(
                document.getElementById("precioVenta").value);

        const precioCompra =
            parseFloat(
                document.getElementById("precioCompra").value || 0);

        const stock =
            parseInt(
                document.getElementById("stockActual").value);

        if(nombre===""){

            alert("Debe ingresar el nombre del producto.");

            e.preventDefault();

            return;

        }

        if(precioVenta<=0){

            alert("El precio de venta debe ser mayor que cero.");

            e.preventDefault();

            return;

        }

        if(precioCompra<0){

            alert("El precio de compra no puede ser negativo.");

            e.preventDefault();

            return;

        }

        if(stock<0){

            alert("El stock no puede ser negativo.");

            e.preventDefault();

            return;

        }

    });

}

/*=========================================
    FUTURAS FUNCIONES
=========================================*/

// actualizarTabla();

// cargarCategorias();

// cargarProducto();

// vista previa imagen

// exportar excel

// exportar pdf

// lector codigo barras

// ajax guardar producto

// ajax editar

// ajax eliminar

// ajax oferta
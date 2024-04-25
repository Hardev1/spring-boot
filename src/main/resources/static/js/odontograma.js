// Utilizar const y let para declarar variables inmutables y mutables respectivamente
const buttons = Array.from(document.getElementsByClassName('tooth'));
const dienteSeleccionadoText = document.getElementById('dienteSeleccionadoText');
const dienteSeleccionado = document.getElementById('dienteSeleccionado');
const nuevoButton = document.getElementById('nuevoButton');
const estadoDental = document.getElementById('estadoDental');
const errorMessage = document.getElementById('errorMessage');
const pacienteSelect = document.getElementById('paciente');
const odontologoSelect = document.getElementById('odontologo');
const dientesEvaluadosList = document.getElementById('dientesEvaluadosList');
const formulario = document.querySelector('form');

// Ocultar y deshabilitar el botón "Nuevo diente a evaluar" inicialmente
nuevoButton.style.display = 'none';
nuevoButton.disabled = true;

// Función de manejo de eventos para el botón
function procesarBoton(btn) {
	  // Remover la clase 'selected' de todos los botones
	  	buttons.forEach(button => button.classList.remove('selected'));
	
	  // Agregar la clase 'selected' al botón actual
	  	btn.classList.add('selected');
	
	  	const botonSeleccionado = btn.value;
	
	  // Mostrar el elemento que indica cuál fue el diente seleccionado
	  	dienteSeleccionadoText.style.display = 'block';
	
	  // Actualizar el texto con el diente seleccionado
	  	dienteSeleccionado.innerText = botonSeleccionado;
	
	  // Mostrar y habilitar el botón "Nuevo diente a evaluar" si hay un diente seleccionado
	  	nuevoButton.style.display = 'block';
	  	nuevoButton.disabled = false;
	
	  // Ocultar el mensaje de error
		errorMessage.style.display = 'none';
	
	  // Mostrar el campo de selección de estado dental
	 	estadoDentalContainer.style.display = 'block';
	
	  // Configuración de la solicitud fetch (opcional)
	 	const requestOptions = {
	    	method: 'POST',
	   	 headers: {
	    	  'Content-Type': 'application/x-www-form-urlencoded'
	    	},
	   	 body: `botonSeleccionado=${encodeURIComponent(botonSeleccionado)}`
	  	};
	
	  // Realizar la solicitud fetch (opcional)
	  fetch('/odontograma/procesar-boton', requestOptions)
	    .then(response => {
	      if (response.ok) {
	        return response.text();
	      }
	      throw new Error('Error en la solicitud.');
	    })
	    .then(data => {
	      console.log('Respuesta del servidor:', data);
	      // Puedes hacer cualquier acción adicional aquí según la respuesta del servidor
	    })
	    .catch(error => {
	      console.error('Error:', error);
	    });
	}



function agregarDienteEvaluado() {
  	const dienteSeleccionado = document.getElementById('dienteSeleccionado').textContent;
  	const estadoDentalValue = document.getElementById('estadoDental').value;

  	const notaDienteElement = document.querySelector('#estadoDentalContainer .row');
  	const notaDienteTexto = notaDienteElement ? notaDienteElement.querySelector('#notaDiente_texto') : null;

  	if (dienteSeleccionado && estadoDentalValue) {
    	let notaDiente = '';
    	if (notaDienteTexto) {
    	  notaDiente = notaDienteTexto.value.trim();
    	}

    const nuevoDienteEvaluado = `${dienteSeleccionado} - ${estadoDentalValue}${notaDiente ? ' - Nota: ' + notaDiente : ' - Nota: Sin notas adicionales'}`;
    const dientesEvaluadosList = document.getElementById('dientesEvaluadosList');

    // Crear el elemento de lista li
    const nuevoItem = document.createElement('li');
    nuevoItem.textContent = nuevoDienteEvaluado;

    // Agregar el elemento de lista li a la lista ul
    dientesEvaluadosList.appendChild(nuevoItem);

    // Actualizar el campo oculto con los dientes evaluados
    actualizarCampoOculto();

    // Limpiar los campos después de agregar el diente evaluado
    limpiarCampos();
  } else {
    // Mostrar el mensaje de error
    errorMessage.style.display = 'block';
  }
}

function actualizarCampoOculto() {
  const dientesEvaluadosInput = document.getElementById('dientesEvaluados');
  const dientesEvaluadosListItems = document.querySelectorAll('#dientesEvaluadosList li');

  const dientesEvaluados = Array.from(dientesEvaluadosListItems).map(item => {
    const dienteInfo = item.textContent.trim();

    return `${dienteInfo}`; // Incluir la nota si está presente
  }).join(',');

  dientesEvaluadosInput.value = dientesEvaluados;
}

function limpiarCampos() {
  document.getElementById('dienteSeleccionado').textContent = '';
  document.getElementById('estadoDental').value = '';

  const notaDienteTexto = document.querySelector('#estadoDentalContainer #notaDiente_texto');
  if (notaDienteTexto) {
    notaDienteTexto.value = '';
  }

  ocultarDienteSeleccionado();
  ocultarNuevoButton();
}



function ocultarDienteSeleccionado() {
	document.getElementById('dienteSeleccionadoText').style.display = 'none';
	document.getElementById('estadoDentalContainer').style.display = 'none';
	ocultarNuevoButton();
}

function ocultarNuevoButton() {
	nuevoButton.style.display = 'none';
	nuevoButton.disabled = true;
}

estadoDental.addEventListener('change', () => {
	const dienteSeleccionadoTexto = document.getElementById('dienteSeleccionado').textContent;
	const estadoDentalValue = estadoDental.value;

	if (dienteSeleccionadoTexto && estadoDentalValue) {
		nuevoButton.style.display = 'block';
		nuevoButton.disabled = false;
		// Ocultar el mensaje de error
		errorMessage.style.display = 'none';
	} else {
		ocultarNuevoButton();
	}
});

// Agregar event listener al formulario para validar antes de enviar
formulario.addEventListener('submit', function(event) {
	const pacienteSeleccionado = pacienteSelect.value;
	const odontologoSeleccionado = odontologoSelect.value;
	const dientesEvaluados = dientesEvaluadosList.children.length;

	if (!pacienteSeleccionado || !odontologoSeleccionado || dientesEvaluados === 0) {
		event.preventDefault(); // Evitar el envío del formulario

		// Mostrar el mensaje de error
		errorMessage.style.display = 'block';
		errorMessage.textContent = 'Por favor, seleccione un paciente, un odontólogo y al menos un diente evaluado antes de guardar.';
	}
});
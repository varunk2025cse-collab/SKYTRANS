// ============================================================
// SKYTRANS - page scripts
// The browser only collects input and shows results.
// All business rules (capacity, safety limits, amounts, accounting)
// are checked by the Spring Boot backend.
// ============================================================

// Sends JSON to the backend and returns the saved data.
// Throws an Error with a readable message when something goes wrong.
async function sendJson(url, method, payload) {
	let response;
	try {
		response = await fetch(url, {
			method: method,
			headers: { 'Content-Type': 'application/json' },
			body: payload ? JSON.stringify(payload) : undefined
		});
	} catch (networkError) {
		throw new Error('Cannot reach the server. Please check that the application is running.');
	}

	if (response.ok) {
		return response;
	}

	// The backend (GlobalExceptionHandler) returns errors as {"message": "..."}
	let message = 'Server error (' + response.status + '). Please try again.';
	try {
		const data = await response.json();
		if (data.message) {
			message = data.message;
		}
	} catch (parseError) {
		// The response was not JSON: keep the default message
	}
	throw new Error(message);
}

// Turns form fields into a JSON object.
// A field named "cableCar.id" becomes { cableCar: { id: 1 } }.
// Empty fields are left out so the backend can use its own defaults.
function formToJson(form) {
	const data = {};
	for (const field of form.elements) {
		if (!field.name || field.value === '') {
			continue;
		}
		let value = field.value;
		if (field.type === 'number' || field.name.endsWith('.id')) {
			value = Number(value);
		}
		setNestedValue(data, field.name, value);
	}
	return data;
}

function setNestedValue(target, path, value) {
	const parts = path.split('.');
	let current = target;
	for (let i = 0; i < parts.length - 1; i++) {
		if (!current[parts[i]]) {
			current[parts[i]] = {};
		}
		current = current[parts[i]];
	}
	current[parts[parts.length - 1]] = value;
}

function showMessage(element, text, type) {
	if (!element) {
		return;
	}
	element.textContent = text;
	element.className = element.className.replace(/message-\w+/g, '').trim() + ' message-' + type;
}

function setLoading(button, isLoading, loadingText) {
	if (!button) {
		return;
	}
	if (isLoading) {
		button.dataset.originalText = button.textContent;
		button.textContent = loadingText;
		button.disabled = true;
	} else {
		button.textContent = button.dataset.originalText || button.textContent;
		button.disabled = false;
	}
}

// Reloads the page so the Thymeleaf tables show the new data
function reloadSoon() {
	setTimeout(function () { window.location.reload(); }, 900);
}

// ---------- Forms ----------
// Every form with class "api-form" is saved to the URL in its data-url attribute.
async function handleFormSubmit(event) {
	event.preventDefault();
	const form = event.target;
	const message = form.querySelector('.message');
	const button = form.querySelector('button[type="submit"]');

	// Basic browser checks (required fields, min/max) before calling the server
	if (!form.reportValidity()) {
		showMessage(message, 'Please correct the highlighted fields.', 'error');
		return;
	}

	setLoading(button, true, 'Saving...');
	showMessage(message, 'Saving...', 'info');
	try {
		await sendJson(form.dataset.url, form.dataset.method || 'POST', formToJson(form));
		showMessage(message, form.dataset.success || 'Saved successfully.', 'success');
		form.reset();
		reloadSoon();
	} catch (error) {
		showMessage(message, error.message, 'error');
	} finally {
		setLoading(button, false);
	}
}

// ---------- Row action buttons (Start trip, Approve, Resolve ...) ----------
async function handleActionClick(event) {
	const button = event.target;
	const message = document.getElementById('pageMessage');
	setLoading(button, true, 'Working...');
	try {
		await sendJson(button.dataset.url, 'POST');
		showMessage(message, button.dataset.success || 'Done.', 'success');
		reloadSoon();
	} catch (error) {
		showMessage(message, error.message, 'error');
		setLoading(button, false);
	}
}

document.addEventListener('DOMContentLoaded', function () {
	document.querySelectorAll('form.api-form').forEach(function (form) {
		// Let our submit handler run the browser checks itself (reportValidity),
		// so the form can also show its own error message.
		form.noValidate = true;
		form.addEventListener('submit', handleFormSubmit);
	});
	document.querySelectorAll('button.action-button').forEach(function (button) {
		button.addEventListener('click', handleActionClick);
	});
});

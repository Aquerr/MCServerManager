$("#add-new-user").on("click", function () {
   $("#add-user-modal").modal("show");
});

function editUser(editButton) {
   const userId = editButton.getAttribute("user-id");
   console.log("Edit user id = " + userId);

   $.ajax({
      method: "GET",
      url: "/api/config/users/" + userId,
      success: function (response) {
         $("#user-id").val(response.id);
         $("#username").val(response.username);
         $("#role").val(response.role);
         $("#add-user-modal").modal("show");

         const currentUserRole = $("#current-user-role").val();

         if(response.role === currentUserRole) {
            $("#role").parent().hide();
         } else {
            $("#role").parent().show();
         }
      }
   });
}

function deleteUser(deleteButton) {
   const userId = deleteButton.getAttribute("user-id");
   console.log(userId);

   $.ajax({
      method: "DELETE",
      url: "/api/config/users/" + userId,
      success: function (response) {
         window.location.reload();
      }
   });
}

$("#user-submit").on("click", function () {
   const userIdElement = $("#user-id");
   const userNameElement = $("#username");
   const passwordElement = $("#password");
   const confirmPasswordElement = $("#confirm-password");
   const roleElement = $("#role");

   const userNameValidated = validateUsername(userNameElement);
   const passwordsValidated = validatePasswords(passwordElement, confirmPasswordElement);

   if (!userNameValidated || !passwordsValidated) {
      return;
   }

   const userId = userIdElement.val();
   const username = userNameElement.val().trim();
   const password = passwordElement.val().trim();
   const role = roleElement.val();
   console.log(role);

   const user = {};
   user["id"] = userId;
   user["username"] = username;
   user["password"] = password;
   user["role"] = role;

   if (userId > 0) {
      updateUser(user);
   } else {
      addUser(user);
   }
});

function addUser(user) {
   $.ajax({
      method: "POST",
      url: "/api/config/users",
      data: JSON.stringify(user),
      contentType: "application/json",
      success: function (response) {
         window.location.reload();
      },
      error: function (response) {
         //TODO: Show error in add user dialog.
         console.log(response);
         alert(response.responseText);
      }
   });
}

function updateUser(user) {
   $.ajax({
      method: "PUT",
      url: "/api/config/users/" + user.id,
      data: JSON.stringify(user),
      contentType: "application/json",
      success: function (response) {
         window.location.reload();
      },
      error: function (response) {
         //TODO: Show error in add user dialog.
         console.log(response);
         alert(response.responseText);
      }
   });
}

function validateUsername(userNameElement) {
   const username = userNameElement.val();
   userNameElement.removeClass("is-invalid");
   userNameElement.removeClass("is-valid");

   let isValid = true;

   if (username === "") {
      isValid = false;
   }

   if (!isValid) {
      userNameElement.addClass("is-invalid");
   } else {
      userNameElement.addClass("is-valid");
   }
   return isValid;
}

function validatePasswords(passwordElement, confirmPasswordElement) {
   const password = passwordElement.val();
   const confirmPassword = confirmPasswordElement.val();
   passwordElement.removeClass("is-invalid");
   passwordElement.removeClass("is-valid");
   confirmPasswordElement.removeClass("is-invalid");
   confirmPasswordElement.removeClass("is-valid");

   let isValid = true;

   if (password === "") {
      passwordElement.addClass("is-invalid");
      isValid = false;
   } else {
      passwordElement.addClass("is-valid");
   }

   if (confirmPassword === "" || confirmPassword !== password) {
      confirmPasswordElement.addClass("is-invalid");
      isValid = false;
   } else {
      confirmPasswordElement.addClass("is-valid");
   }
   return isValid;
}
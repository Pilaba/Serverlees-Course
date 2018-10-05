const functions = require('firebase-functions');
const admin = require('firebase-admin');
const bluebird = require('bluebird');

admin.initializeApp();

//OBTENER TODOS LOS USUARIOS AUTENTICADOS
exports.getAllUsers = functions.https.onRequest((req, res) => {
    admin.auth().listUsers().then( (usuarios) => {
        res.json(usuarios)
    }).catch((error) => {
        res.json(error)
    });
});

//TRIGGER ON AUTH SUCCESSFULL
exports.OnAuthOK = functions.auth.user().onCreate( (usuario) => {

    let nombre = usuario.displayName;
    let photoURL =  usuario.photoURL;

    if(nombre == null){
        nombre = usuario.email == null ? usuario.phoneNumber : usuario.email;
    }
    if(photoURL != null){
        return admin.database().ref("usuarios/"+usuario.uid).set({
            "UID": usuario.uid, "Nombre": nombre.toLowerCase(), "PhotoURL": photoURL.toString()
        });
    } else {
        return admin.database().ref("usuarios/"+usuario.uid).set({
            "UID": usuario.uid, "Nombre": nombre.toLowerCase()
        });
    }

});

//SEND PUSH NOTIFICATION
exports.sendMessageNotification = functions.database.ref("mensajes/{idMensaje}").onCreate((dbSnapshot, contexto) => {
    //Mensaje enviado
    let mensaje = dbSnapshot.val();

    let tachas = admin.database().ref("usuarios/"+mensaje.UID_DE).once("value");
    let perico = admin.database().ref("usuarios/"+mensaje.UID_PARA).once("value");
    return Promise.all([tachas, perico]).then( data => {
        let userEnvia = data[0].val();
        let userRecibe = data[1].val();

        if(userRecibe.token == null){
            throw new Error('no hay token, no se envia la notificacion push - tachas y perico');
        }
        const payload = {
            notification: {
                title: userEnvia.Nombre,
                icon : userRecibe.PhotoURL != null ? userRecibe.PhotoURL : null,
                body: mensaje.cuerpo
            }
        };
        console.log("tashas y perico - enviando notificacion push a ", userRecibe.Nombre);
        return admin.messaging().sendToDevice(userRecibe.token, payload);
    });

});


const jwt = require('jsonwebtoken');

const fakeUser = {
    email: 'user@example.com',
    password: 'password',
};

const secretKey = 'NOIBIOifnIOnvoilvo2r2893792ofnoilfn';

function generateToken(user) {
    return jwt.sign({email: user.email}, secretKey, {expiresIn: '1h'});
}

function authenticate({email, password}) {
    if (email === fakeUser.email && password === fakeUser.password) {
        return generateToken(fakeUser);
    } else {
        return false;
    }
}
module.exports = {authenticate};

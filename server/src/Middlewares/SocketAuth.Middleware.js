import jwt from 'jsonwebtoken'

// Middleware to verify the token
function verifyToken(req, res, next) {
  const token = req.headers['authorization']?.split(' ')[1]; // Assuming Bearer token

  if (!token) {
    return res.status(403).send('Token is required');
  }

  jwt.verify(token, 'your_secret_key', (err, decoded) => {
    if (err) {
      return res.status(401).send('Invalid token');
    }
    req.user = decoded; // Add user info to request object
    next(); // Proceed to the next middleware or route
  });
}

export default verifyToken;

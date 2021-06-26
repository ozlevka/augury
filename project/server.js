const express = require('express')
const app = express()
const port = 8080

app.get('/', (req, res) => {
  res.send('Hello World!')
})

app.get("/test", (req, res) => {
    res.send("OK");
});



app.listen(port, '0.0.0.0', () => {
   console.log(`Example app listening at http://0.0.0.0:${port}`)
})
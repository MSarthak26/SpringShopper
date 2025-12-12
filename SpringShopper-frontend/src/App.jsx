import './App.css'
import React from 'react';
import Home from './components/home/home';
import Products from './components/products/Products';
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Navbar from './components/shared/Navbar';
import About from './components/About';
import Contact from './components/contact';
import { Toaster } from 'react-hot-toast';
import Cart from './components/cart/Cart';
import Login from './components/auth/Login';
import PrivateRoute from './components/PrivateRoute';
import Register from './components/auth/Register';
import Checkout from './components/checkout/checkout';


function App() {

  return (
    <React.Fragment>    
        <Router>
          <Navbar/>
          <Routes>
            <Route path='/' element={<Home/>}/>
            <Route path='/products' element={<Products/>}/>
            <Route path='/about' element={<About/>}/>
            <Route path='/contact' element={<Contact/>}/>
            <Route path='/cart' element={<Cart/>}/>
            <Route path='/checkout' element={<Checkout/>}/>
            
            <Route path='/' element={<PrivateRoute publicPage />}>
                <Route path='/login' element={<Login/>}/>
                <Route path='/register' element={<Register/>}/>
            </Route>
          </Routes>
        </Router>
      <Toaster position='bottom-center' />
    </React.Fragment>
  )
}

export default App

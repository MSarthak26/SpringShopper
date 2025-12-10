import './App.css'
import Home from './components/home/home';
import Products from './components/products/Products';
import { BrowserRouter as Router, Routes, Route} from 'react-router-dom';
import Navbar from './components/shared/Navbar';
import About from './components/About';
import Contact from './components/contact';


function App() {

  return (
    <Router>
      <Navbar/>
      <Routes>
        <Route path='/' element={<Home/>}/>
        <Route path='/products' element={<Products/>}/>
        <Route path='/about' element={<About/>}/>
        <Route path='/contact' element={<Contact/>}/>
      </Routes>
    </Router>
  )
}

export default App

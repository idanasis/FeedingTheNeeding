import React from 'react';
import HomePage from './Home/HomePage';
import NeederTracking from './Social/components/NeederTracking';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import DonorRegister from "./Authentication/Components/DonorRegister";
import Login from "./Authentication/Components/Login";



function App() {
  return (
  <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/social" element={<NeederTracking />} />
            <Route path="/login" element={<Login />} />
            <Route path="/donorRegister" element={<DonorRegister />} />
        </Routes>
      </Router>
  );
}

export default App;
import React from 'react';
import HomePage from './Home/HomePage';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';



function App() {
  return (
  <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
        </Routes>
      </Router>
  );
}

export default App;
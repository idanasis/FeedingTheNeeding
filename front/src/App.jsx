import React from 'react';
import HomePage from './Home/HomePage';
import NeederTracking from './Social/components/NeederTracking';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';



function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/social" element={<NeederTracking />} />
            </Routes>
        </Router>
    );
}

export default App;
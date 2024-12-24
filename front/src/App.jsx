import React from 'react';
import HomePage from './Home/HomePage';
import NeederTracking from './Social/components/NeederTracking';
import ResponsiveNeedyTable from './Social/components/needyPending';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from "./Authentication/Components/Login";
import DonorRegister from "./Authentication/Components/DonorRegister";
import UploadConstraints from "./Cooking/Components/UploadConstraints";

function App() {
  return (
  <Router>
        <Routes>
          <Route path="/" element={<UploadConstraints />} />
          <Route path="/social" element={<NeederTracking />} />
          <Route path="/neederPending" element={<ResponsiveNeedyTable />} />
            <Route path="/login" element={<Login />} />
            <Route path="/donorRegister" element={< DonorRegister />} />
        </Routes>
      </Router>
  );
}

export default App;
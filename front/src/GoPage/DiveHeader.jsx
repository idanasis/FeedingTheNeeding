import React, { useState, useEffect } from 'react';
import { Link } from 'react-scroll';

const Header = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    setIsLoggedIn(!!token);
  }, []);

  const closeMenu = () => {
    setMenuOpen(false);
  };

  
  return (
    <nav className='w-full flex bg-white justify-between items-center gap-1 lg:px-16 px-6 py-4 sticky top-0 z-50'>
      <h1 className='text-black md:text-4xl text-3xl font-bold cursor-pointer' onClick={() => window.location.href = '/'}>
        להשביע את הלב
      </h1>

      {/* Add Actions Button if logged in */}
      {isLoggedIn && (
        <div className='flex items-center gap-4'>
          <button
            className='bg-red-500 hover:bg-black hover:text-white text-black px-10 py-3 rounded-full font-semibold transform hover:scale-105 transition-transform duration-300 cursor-pointer'
            onClick={() => window.location.href = '/go'}
          >
            לפעולות
          </button>
        </div>
      )}
      
      
    </nav>
  );
}

export default Header;
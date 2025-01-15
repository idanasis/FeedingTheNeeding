import React, { useState, useEffect } from 'react';
import { FaXmark, FaBars } from 'react-icons/fa6';
import { Link } from 'react-scroll';

const Header = () => {
  const [isMenuOpen, setMenuOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    setIsLoggedIn(!!token);
  }, []);

  const toggleMenu = () => {
    setMenuOpen(!isMenuOpen);
  };

  const closeMenu = () => {
    setMenuOpen(false);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setIsLoggedIn(false);
    window.location.href = '/';
  };

  const handleButtonClick = () => {
    if (isLoggedIn) {
      window.location.href = '/go';
    } else {
      window.location.href = '/login';
    }
  };

  const navItems = [
    { name: 'אודות', path: 'אודות' },
    { name: 'פרוייקטים', path: 'פרוייקטים' },
    { name: 'תמונות', path: 'תמונות' },
    { name: 'לתרומות', path: 'לתרומות' },
    { name: 'לבקשת סיוע', path: 'בקשת סיוע' },
  ];

  return (
    <nav className='w-full flex bg-white justify-between items-center gap-1 lg:px-16 px-6 py-4 sticky top-0 z-50'>
      <h1
        className='text-black md:text-4xl text-3xl font-bold cursor-pointer'
        onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}
      >
        להשביע את הלב
      </h1>
      <ul className='lg:flex justify-center items-center gap-6 hidden'>
        {navItems.map(({ name, path }) => (
          <Link
            key={path}
            to={path}
            className='text-black uppercase font-bold cursor-pointer p-3 rounded-full hover:bg-red-500 hover:text-black text-[15px]'
            spy={true}
            offset={-100}
            smooth={true}
            duration={500}
          >
            {name}
          </Link>
        ))}
      </ul>
      {/* Desktop buttons */}
      <div className='hidden lg:flex items-center gap-4'>
        <button
          className='bg-red-500 hover:bg-black hover:text-white text-black px-10 py-3 rounded-full font-semibold transform hover:scale-105 transition-transform duration-300 cursor-pointer'
          onClick={handleButtonClick}
        >
          {isLoggedIn ? 'לפעולות' : 'בואו להתנדב!'}
        </button>
        {isLoggedIn && (
          <button
            className='bg-gray-400 hover:bg-black hover:text-white text-black px-6 py-3 rounded-full font-semibold transform hover:scale-105 transition-transform duration-300 cursor-pointer'
            onClick={handleLogout}
          >
            התנתקות
          </button>
        )}
      </div>
      {/* Mobile menu toggle */}
      <div className='lg:hidden mt-3' onClick={toggleMenu}>
        {isMenuOpen ? (
          <FaXmark className='text-red-500 text-3xl cursor-pointer' />
        ) : (
          <FaBars className='text-red-500 text-3xl cursor-pointer' />
        )}
      </div>
      {/* Mobile menu */}
      <div
        className={`${
          isMenuOpen ? 'flex' : 'hidden'
        } w-full h-fit bg-red-500 p-4 absolute top-[72px] left-0`}
      >
        <ul className='flex flex-col justify-center items-center gap-2 w-full'>
          {navItems.map(({ name, path }) => (
            <Link
              key={path}
              to={path}
              className='text-black uppercase font-semibold cursor-pointer p-3 rounded-lg hover:bg-black hover:text-white text-center w-full'
              spy={true}
              offset={-100}
              smooth={true}
              duration={500}
              onClick={closeMenu}
            >
              {name}
            </Link>
          ))}
          {/* Mobile buttons */}
          <div className='w-full flex flex-col items-center gap-3 mt-4'>
            <button
              className='bg-black text-white px-10 py-3 rounded-full font-semibold transform hover:scale-105 transition-transform duration-300 cursor-pointer'
              onClick={handleButtonClick}
            >
              {isLoggedIn ? 'לפעולות' : 'בואו להתנדב!'}
            </button>
            {isLoggedIn && (
              <button
                className='bg-gray-400 hover:bg-black hover:text-white text-black px-10 py-3 rounded-full font-semibold transform hover:scale-105 transition-transform duration-300 cursor-pointer'
                onClick={handleLogout}
              >
                התנתקות
              </button>
            )}
          </div>
        </ul>
      </div>
    </nav>
  );
};

export default Header;

import React, { useState, useEffect } from 'react';
import { FaXmark, FaBars } from 'react-icons/fa6';

const DiveHeader = () => {
  const [isMenuOpen, setMenuOpen] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    setIsLoggedIn(!!token);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    setIsLoggedIn(false);
    window.location.href = '/';
  };

  const handleButtonClick = () => {
    window.location.href = isLoggedIn ? '/go' : '/login';
  };

  // Extracted button component for reusability
  const ActionButton = ({ onClick, className, children }) => (
    <button
      onClick={onClick}
      className={`px-10 py-3 rounded-full font-semibold transform hover:scale-105 transition-transform duration-300 cursor-pointer ${className}`}
    >
      {children}
    </button>
  );

  // Desktop navigation buttons
  const DesktopButtons = () => (
    <div className="hidden lg:flex items-center gap-4">
      <ActionButton
        onClick={handleButtonClick}
        className="bg-red-500 hover:bg-black hover:text-white text-black"
      >
        {isLoggedIn ? 'לפעולות' : 'בואו להתנדב!'}
      </ActionButton>
      {isLoggedIn && (
        <ActionButton
          onClick={handleLogout}
          className="bg-gray-400 hover:bg-black hover:text-white text-black"
        >
          התנתקות
        </ActionButton>
      )}
    </div>
  );

  // Mobile menu buttons
  const MobileMenu = () => (
    <div
      className={`${
        isMenuOpen ? 'flex' : 'hidden'
      } w-full h-fit bg-red-500 p-4 absolute top-[72px] left-0`}
    >
      <div className="w-full flex flex-col items-center gap-3 mt-4">
        <ActionButton
          onClick={handleButtonClick}
          className="bg-black text-white w-full"
        >
          {isLoggedIn ? 'לפעולות' : 'בואו להתנדב!'}
        </ActionButton>
        {isLoggedIn && (
          <ActionButton
            onClick={handleLogout}
            className="bg-gray-400 hover:bg-black hover:text-white text-black w-full"
          >
            התנתקות
          </ActionButton>
        )}
      </div>
    </div>
  );

  return (
    <nav className="w-full flex bg-white justify-between items-center gap-1 lg:px-16 px-6 py-4 sticky top-0 z-50">
      <h1
        className="text-black md:text-4xl text-3xl font-bold cursor-pointer"
        onClick={() => window.location.href = '/'}
      >
        להשביע את הלב
      </h1>

      <div className="flex items-center gap-4">
        <DesktopButtons />
        
        {/* Mobile menu toggle */}
        <div className="lg:hidden mt-3" onClick={() => setMenuOpen(!isMenuOpen)}>
          {isMenuOpen ? (
            <FaXmark className="text-red-500 text-3xl cursor-pointer" />
          ) : (
            <FaBars className="text-red-500 text-3xl cursor-pointer" />
          )}
        </div>

        <MobileMenu />
      </div>
    </nav>
  );
};

export default DiveHeader;
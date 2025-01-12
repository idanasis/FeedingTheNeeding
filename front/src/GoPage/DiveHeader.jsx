import React, { useState } from 'react';
import { Link } from 'react-scroll';

const Header = () => {
  const [isMenuOpen, setMenuOpen] = useState(false);

 
  const closeMenu = () => {
    setMenuOpen(false);
  }

  const navItems = [
   
  ];

  return (
    <nav className='w-full flex bg-white justify-between items-center gap-1 lg:px-16 px-6 py-4 sticky top-0 z-50'>
      <h1 className='text-black md:text-4xl text-3xl font-bold cursor-pointer' onClick={() => window.location.href = '/'}>
        להשביע את הלב
      </h1>
     
      {/* Add menu toggle icon and mobile menu here */}
      
     
      <div className={`${isMenuOpen ? 'flex':'hidden'} w-full h-fit bg-red-500 p-4 absolute top-[72px] left-0`}>
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
        </ul>
      </div>
    </nav>
  );
}

export default Header;

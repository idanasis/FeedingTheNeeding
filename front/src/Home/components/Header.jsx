import React, { useState } from 'react';
import { FaXmark, FaBars } from 'react-icons/fa6';
import { Link } from 'react-scroll';

const Header = () => {
  const [isMenuOpen, setMenuOpen] = useState(false);

  const toggleMenu = () => {
    setMenuOpen(!isMenuOpen);
  }

  const closeMenu = () => {
    setMenuOpen(false);
  }

  const navItems = [
    { name: 'בית', path: 'בית' },
    { name: 'אודות', path: 'אודות' },
    { name: 'פרוייקטים', path: 'פרוייקטים' },
    { name: 'תמונות', path: 'תמונות' },
    { name: 'לבקשת סיוע', path: 'בקשת סיוע' }
  ];

  return (
    <nav className='w-full flex bg-white justify-between items-center gap-1 lg:px-16 px-6 py-4 sticky top-0 z-50'>
      <h1 className='text-black md:text-4xl text-3xl font-bold'>להשביע את הלב</h1>
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
      {/* Add menu toggle icon and mobile menu here */}
      <button className='bg-red-500 hover:bg-black hover:text-white text-black px-10 py-3 rounded-full font-semibold transform hover:scale-105 transition-transform duration-300 cursor-pointer flex'>
        בואו להתנדב!
      </button>
      <div className='lg:hidden mt-3' onClick={toggleMenu}>
        {isMenuOpen ? <FaXmark className='text-red-500 text-3xl cursor-pointer'/> : <FaBars className='text-red-500 text-3xl cursor-pointer'/>}
      </div>
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

import React from 'react';
import { FaArrowUp, FaCopyright } from 'react-icons/fa6';
import { Link } from 'react-scroll';

const Footer = () => {
  return (
    <>
      {/* Footer Section */}
      <div className='bg-black text-white flex justify-center items-center gap-2 p-5'>
        <FaCopyright className='fill-red-500 text-xl lg:text-2xl' />
        <p>Copyright 2024, Shavit & Idan & Daniel & Eden, All Rights Reserved</p>
      </div>

      {/* Back to Top Arrow */}
      <div
        id='icon-box'
        className='bg-red-500 text-black p-3 rounded-full hover:bg-black hover:text-white cursor-pointer fixed lg:bottom-2 bottom-2 right-6 flex justify-center items-center'
      >
        <Link
          to='hero'
          spy={true}
          offset={-100}
          smooth={true}
          duration={500}
          className='flex justify-center items-center'
        >
          <FaArrowUp className='text-2xl' />
        </Link>
      </div>
    </>
  );
};

export default Footer;

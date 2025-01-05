import React from 'react'
import { motion } from 'framer-motion';
import { slideUpVariants, zoomInVariants } from './animation';
const Contact = () => {
  return (
    <div  id='בקשת סיוע' >
    <div className='lg:w-[80%] w-[90%] m-auto py-[60px] flex lg:flex-row flex-col items-start gap-[50px] justify-between'>
      <motion.div
        initial="hidden" whileInView="visible" variants={slideUpVariants} className='lg:w-[60%] w-full flex flex-col justify-center items-start gap-6'>
        <motion.h1 variants={slideUpVariants} className='text-red-500 text-3xl' >
          לבקשת סיוע
        </motion.h1>
        <motion.h1 variants={slideUpVariants} className='text-white uppercase text-[40px] font-bold' >
          אנחנו כאן לעזור
        </motion.h1>
        <div className='w-[120px] h-[6px] bg-red-500'></div>
        <p className='text-3xl italic text-red-100  mt-[60px]'> אם אתם מתקשים כלכלית ורוצים עזרה מהעמותה שלנו, זה המקום לבקש. מי שבמקש יכנס לרשימת המתנה, במידה ויהיה מקום וימצא מתאים ניצור איתכם קשר.</p>
      </motion.div>
      <motion.div
        initial="hidden" whileInView="visible"
        variants={slideUpVariants} className='lg:w-[40%] w-full flex flex-col justify-center items-start gap-6'
      >
        <motion.form initial="hidden" whileInView="visible" variants={zoomInVariants} className='flex flex-col justify-center items-start gap-4 w-full' >
          <input type="text" placeholder='הכנס שם מלא' className='px-6 py-3 border-[2px] border-black text-black rounded-lg w-full' />
          <input type="email" placeholder='הכנס מייל' className='px-6 py-3 border-[2px] border-black text-black rounded-lg w-full' />
          <input type="number" placeholder='הכנס פלאפון' className='px-6 py-3 border-[2px] border-black text-black rounded-lg w-full' />
          <textarea name="" placeholder='הערות נוספות' id='' rows='4' className='px-6 py-3 border-[2px] border-black text-black rounded-lg w-full'></textarea>
          <motion.button variants={zoomInVariants} className='bg-red-500 hover:bg-black hover:text-white px-10 py-4 rounded-lg text-black font-bold'>שליחה</motion.button>
        </motion.form>
      </motion.div>
    </div>
    </div>
  )
}

export default Contact;
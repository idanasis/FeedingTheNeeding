import React, { useEffect, useState } from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { TableVirtuoso, TableComponents } from 'react-virtuoso';
import { Box, Button } from '@mui/material';
import { Donor } from '../../models/DonorModel';
import { deleteDonor, getPendingDonors, updateDonor } from '../../Restapi/DonorRestapi';



const DonorPendingTable = () => {
  const [currNeeders, setCurrNeeders] = useState<Donor[]>([]);
  async function fetchDonors() {
    try{
      const data=await getPendingDonors();
      console.log(data)
      setCurrNeeders(data)
    }catch(err){
        console.log(err)
      alert('אין נתונים להצגה')
      }
    }

  useEffect(() => {
    fetchDonors();
  }, []);

    const handleAccept = async(donor:Donor) => {
      try{
        donor.status="AVAILABLE";
        await updateDonor(donor);
        await fetchDonors();
    
      }catch(err){
        alert('אירעה שגיאה')
      }
    }
    const handleReject = async(donor:Donor) => {
      try{
        await deleteDonor(donor);
        await fetchDonors();
    
      }catch(err){
        alert('אירעה שגיאה')
      }
    }
    const VirtuosoTableComponents: TableComponents<Donor> = {
      Scroller: React.forwardRef<HTMLDivElement>((props, ref) => (
        <TableContainer component={Paper} {...props} ref={ref} />
      )),
      Table: (props) => (
        <Table {...props} sx={{ borderCollapse: 'separate', tableLayout: 'fixed' }} />
      ),
      TableHead: React.forwardRef<HTMLTableSectionElement>((props, ref) => (
        <TableHead {...props} ref={ref} />
      )),
      TableRow,
      TableBody: React.forwardRef<HTMLTableSectionElement>((props, ref) => (
        <TableBody {...props} ref={ref} />
      )),
    };
    // Columns Configuration
  interface ColumnData {
    dataKey: keyof Donor;
    label: string;
    numeric?: boolean;
    width?: string;
  }



  // Columns Configuration
    const columns: ColumnData[] = [
      { width: '14%', label: 'שם', dataKey: 'firstName' },
      { width: '22%', label: 'טלפון', dataKey: 'phoneNumber' },
      { width:'13%', label: 'כתובת', dataKey: 'address' },
      { width: '9%', label: 'עיר', dataKey: 'city' },
      { width: '18%', label: 'אימייל', dataKey: 'email'},
    ];
    // Header Content
    function fixedHeaderContent() {
      return (
        <TableRow>
          {columns.map((column) => (
            <TableCell
              key={column.dataKey}
              variant="head"
              align={'right'}
              style={{ width: column.width }}
              sx={{ backgroundColor: 'background.paper', fontWeight: 'bold', fontSize:11}}
            >
              {column.label}
            </TableCell>
          ))}
        </TableRow>
      );
    }
    
    // Row Content
    function rowContent(_index: number, row: Donor) {
      
      return (
        <>
          <TableCell align="right"  sx={{ fontSize: { xs: '9px', sm: '10px', md: '12px' } }}>{row.firstName} {row.lastName}</TableCell>
          <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '10px', md: '12px' } }}>{row.phoneNumber}</TableCell>
          <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '10px', md: '12px' } }}>{row.address}</TableCell>
          <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '10px', md: '12px' } }}>{row.city}</TableCell>
          <TableCell align="right"  sx={{ fontSize: { xs: '9px', sm: '10px', md: '12px' }, paddingRight: '30px'}}>{row.email}</TableCell>
          <TableCell align="center">
          <Box gap={1} justifyContent="center" marginRight={1} sx={{ paddingRight: '30px'}}>
          <Button variant="contained" color="success" onClick={async()=>await handleAccept(row)}>
            אשר
          </Button>
          <Button variant="outlined" color="error" onClick={async()=>await handleReject(row)}>
            דחה
          </Button>
          </Box>
          </TableCell>
        </>
      );
    }
  return (
  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' ,backgroundColor: "snow"}}>
    <h1 style={{ fontSize: '3rem', fontWeight: 'bold', margin: '20px'}}>
      אישור מתנדבים
    </h1>
    <Paper style={{ height: '100vh', width: '100%' }}>
      <TableVirtuoso
        data={currNeeders}
        components={VirtuosoTableComponents}
        fixedHeaderContent={fixedHeaderContent}
        itemContent={rowContent}
      />
    </Paper>
  </div>
  );
};

export default DonorPendingTable;
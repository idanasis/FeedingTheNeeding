import React, { useEffect, useState } from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import { TableVirtuoso, TableComponents } from 'react-virtuoso';
import { Needy } from '@/src/models/NeedyModel';
import { Box, Button, IconButton } from '@mui/material';
import { LoadingButton } from '@mui/lab';
import { deleteNeedy, getNeedyPending, updateNeedy } from '../../Restapi/socialRestapi';
import DiveHeader from '../../GoPage/DiveHeader';




const NeedyPendingTable = () => {
  const [currNeeders, setCurrNeeders] = useState<Needy[]>([]);
  async function fetchNeeders() {
    try{
      const data=await getNeedyPending();
      setCurrNeeders(data)
    }catch(err){
      alert('אין נתונים להצגה')
      }
    }

  useEffect(() => {
      fetchNeeders();
  }, []);

    const handleAccept = async(needy:Needy) => {
      try{
        needy.confirmStatus="APPROVED";
        await updateNeedy(needy);
        await fetchNeeders();
    
      }catch(err){
        alert('אירעה שגיאה')
      }
    }
    const handleReject = async(needy:Needy) => {
      try{
        await deleteNeedy(needy);
        await fetchNeeders();
    
      }catch(err){
        alert('אירעה שגיאה')
      }
    }
    const VirtuosoTableComponents: TableComponents<Needy> = {
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
    dataKey: keyof Needy;
    label: string;
    numeric?: boolean;
    width?: string;
  }



  // Columns Configuration
    const columns: ColumnData[] = [
      { width: '20%', label: 'שם', dataKey: 'firstName' },
      { width: '21%', label: 'טלפון', dataKey: 'phoneNumber' },
      { width:'16%', label: 'כתובת', dataKey: 'address' },
      { width: '14%', label: 'גודל משפחה', dataKey: 'familySize', numeric: true },
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
              sx={{ backgroundColor: 'background.paper', fontWeight: 'bold', fontSize:11 }}
            >
              {column.label}
            </TableCell>
          ))}
        </TableRow>
      );
    }
    
    // Row Content
    function rowContent(_index: number, row: Needy) {
      
      return (
        <>
          <TableCell align="right"  sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{row.firstName} {row.lastName}</TableCell>
          <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{row.phoneNumber}</TableCell>
          <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{row.address}</TableCell>
          <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{row.familySize}</TableCell>
          <TableCell align="center">
          <Box gap={1} justifyContent="center" marginRight={1}>
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
  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' ,backgroundColor: "snow",paddingRight: '20px',paddingLeft: '20px'}}>
    <DiveHeader/>
    <h1 style={{ fontSize: '3rem', fontWeight: 'bold', margin: '20px'}}>
      אישור נזקקים
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

export default NeedyPendingTable;
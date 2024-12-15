import React from 'react';
import PropTypes from 'prop-types';
import Box from '@mui/material/Box';
import Collapse from '@mui/material/Collapse';
import IconButton from '@mui/material/IconButton';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import Paper from '@mui/material/Paper';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import { NeederTrackingModel } from '@/src/models/NeederTrackingModel';
import { useState } from 'react';


function createData(neederTrackingModel: NeederTrackingModel) {
  return {
    id: neederTrackingModel.id,
    name: neederTrackingModel.needy.firstName + ' ' + neederTrackingModel.needy.lastName,
    phoneNumber: neederTrackingModel.needy.phoneNumber,
    address: neederTrackingModel.needy.address,
    city: neederTrackingModel.needy.city,
    status: neederTrackingModel.status,
    details: [
      {
        familySize: neederTrackingModel.needy.familySize,
        foodPreference: neederTrackingModel.foodPreference,
        notes: neederTrackingModel.notes,
      },
    ],
  };
}

const Row= (props: { row: ReturnType<typeof createData> }) =>  {
  const [row,setRow]= useState(props.row);
  const [open, setOpen] = React.useState(false);
  const handleAvailableChange = (index: number, event: React.ChangeEvent<HTMLSelectElement>) => {
    const updatedRow = { ...row, status: event.target.value };
    setRow(updatedRow);
  }
  return (
    <React.Fragment>
      <TableRow sx={{ borderBottom: 'unset' }}>
        <TableCell>
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
        <TableCell align="justify" component="th" scope="row"sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>
          {row.name}
        </TableCell>
        <TableCell align="right"sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' }, whiteSpace: 'nowrap',
    overflow: 'hidden',textOverflow: 'ellipsis'}}>{row.phoneNumber}</TableCell>
        <TableCell align="right"sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{row.address}</TableCell>
        <TableCell align="right"sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{row.city}</TableCell>
        <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>
         <select
            id={`${row.id}`}
            value={row.status}
            onChange={(event) => handleAvailableChange(row.id, event)}
          >
            <option value="זמין">זמין</option>
            <option value="לא זמין">לא זמין</option>
          </select>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <Box sx={{ margin: 1 }}>
              <Typography variant="h6" gutterBottom component="div" sx={{ textAlign: 'right' }}>
                פרטים
              </Typography>
              <Table size="small" aria-label="details">
                <TableHead>
                  <TableRow>
                    <TableCell align="right">גודל משפחה</TableCell>
                    <TableCell align="right">העדפות אוכל</TableCell>
                    <TableCell align="right">הערות</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {row.details.map((detail, index) => (
                    <TableRow key={index}>
                      <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{detail.familySize}</TableCell>
                      <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{detail.foodPreference}</TableCell>
                      <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>{detail.notes}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </React.Fragment>
  );
}

Row.propTypes = {
  row: PropTypes.shape({
    name: PropTypes.string.isRequired,
    phoneNumber: PropTypes.string.isRequired,
    address: PropTypes.string.isRequired,
    city: PropTypes.string.isRequired,
    status: PropTypes.string.isRequired,
    details: PropTypes.arrayOf(
      PropTypes.shape({
        familySize: PropTypes.number.isRequired,
        foodPreference: PropTypes.string.isRequired,
        notes: PropTypes.string.isRequired,
      })
    ).isRequired,
  }).isRequired,
};

const NeederTrackingTable = ({ data }: { data: NeederTrackingModel[] }) => {
  const rows = data.map((item) => createData(item));
  
  return (
    <TableContainer component={Paper}>
      <Table aria-label="collapsible table">
        <TableHead>
          <TableRow>
            <TableCell />
            <TableCell align="right"sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>שם</TableCell>
            <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>טלפון</TableCell>
            <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>כתובת</TableCell>
            <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>עיר</TableCell>
            <TableCell align="right" sx={{ fontSize: { xs: '9px', sm: '12px', md: '12px' } }}>סטטוס</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <Row key={row.id} row={row} />
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default NeederTrackingTable;

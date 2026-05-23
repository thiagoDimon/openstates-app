import { useState } from 'react'
import type { SelectChangeEvent } from '@mui/material'
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  Typography,
} from '@mui/material'
import { US_STATE_OPTIONS } from '@/types/usState'
import { useSyncState } from '@/hooks/useSyncState'

interface SyncDataModalProps {
  open: boolean
  onClose: () => void
}

export function SyncDataModal({ open, onClose }: SyncDataModalProps) {
  const [selectedState, setSelectedState] = useState('')
  const { mutate: sync, isPending, isSuccess, isError, error, reset } = useSyncState()

  const errorMessage = isError && error instanceof Error ? error.message : 'Sync failed. Please try again.'

  function handleClose() {
    setSelectedState('')
    reset()
    onClose()
  }

  function handleConfirm() {
    if (!selectedState) return
    sync(selectedState)
  }

  return (
    <Dialog
      open={open}
      onClose={isPending ? undefined : handleClose}
      maxWidth="sm"
      fullWidth
      slotProps={{ paper: { sx: { borderRadius: 3 } } }}
    >
      <DialogTitle>Sync Politicians Data</DialogTitle>
      <DialogContent sx={{ pt: 2 }}>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
          Select a state to fetch the next page of politicians from the OpenStates API.
        </Typography>
        <FormControl
          fullWidth
          size="small"
          disabled={isPending || isSuccess}
          sx={{ '& .MuiOutlinedInput-root': { borderRadius: '12px' } }}
        >
          <InputLabel>State</InputLabel>
          <Select
            value={selectedState}
            label="State"
            onChange={(e: SelectChangeEvent) => setSelectedState(e.target.value)}
          >
            <MenuItem value="">Select a state</MenuItem>
            {US_STATE_OPTIONS.map((s) => (
              <MenuItem key={s} value={s}>
                {s}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        {isSuccess && (
          <Typography color="success.main" variant="body2" sx={{ mt: 2 }}>
            Sync completed successfully for {selectedState.toUpperCase()}.
          </Typography>
        )}
        {isError && (
          <Typography color="error" variant="body2" sx={{ mt: 2 }}>
            {errorMessage}
          </Typography>
        )}
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 3, justifyContent: 'flex-end' }}>
        <Button
          variant="contained"
          size="medium"
          onClick={handleClose}
          disabled={isPending}
          sx={{
            borderRadius: '50px',
            textTransform: 'none',
            px: 3,
            bgcolor: 'error.main',
            color: 'white',
            '&:hover': { bgcolor: 'error.dark' },
          }}
        >
          {isSuccess ? 'Close' : 'Cancel'}
        </Button>
        {!isSuccess && (
          <Button
            variant="contained"
            size="medium"
            onClick={handleConfirm}
            disabled={!selectedState || isPending}
            loading={isPending}
            sx={{
              borderRadius: '50px',
              textTransform: 'none',
              px: 3,
              bgcolor: 'success.main',
              color: 'white',
              '&:hover': { bgcolor: 'success.dark' },
            }}
          >
            Confirm
          </Button>
        )}
      </DialogActions>
    </Dialog>
  )
}

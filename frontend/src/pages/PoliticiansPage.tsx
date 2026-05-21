import { useState } from 'react'
import { Box, Button, CircularProgress, Container, Typography } from '@mui/material'
import { useFilterOptions, usePoliticians, useSyncPoliticians } from '@/hooks/usePoliticians'
import { FilterBar } from '@/components/FilterBar'
import { PoliticianGrid } from '@/components/PoliticianGrid'

export function PoliticiansPage() {
  const [state, setState] = useState('')
  const [party, setParty] = useState('')

  const { data: filterOptions, isLoading: filtersLoading, isError: filtersError } = useFilterOptions()
  const { data: politicians = [], isLoading, isError } = usePoliticians(
    state || undefined,
    party || undefined,
  )
  const { mutate: sync, isPending: isSyncing } = useSyncPoliticians()

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
        US Politicians
      </Typography>

      <Box sx={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: 2, mb: 3 }}>
        {filtersLoading ? (
          <CircularProgress size={24} />
        ) : filtersError ? (
          <Typography color="error">
            Failed to load filter options. Please refresh the page.
          </Typography>
        ) : filterOptions ? (
          <FilterBar
            filters={filterOptions}
            state={state}
            party={party}
            onStateChange={setState}
            onPartyChange={setParty}
          />
        ) : null}

        <Button
          variant="contained"
          onClick={() => sync()}
          disabled={isSyncing}
          startIcon={isSyncing ? <CircularProgress size={16} color="inherit" /> : undefined}
          sx={{ ml: 'auto' }}
        >
          Sync Data
        </Button>
      </Box>

      <PoliticianGrid
        politicians={politicians}
        isLoading={isLoading}
        isError={isError}
      />
    </Container>
  )
}
